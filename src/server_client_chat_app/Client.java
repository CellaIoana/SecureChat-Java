package server_client_chat_app;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.security.KeyPair;
import java.security.PublicKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private static KeyPair clientKeyPair;
    private static SecretKey secretKey;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server on port " + SERVER_PORT);

            // 1) Receives server's public key
            String serverPublicKeyEncodedStr = in.readLine();
            PublicKey serverPublicKey = DiffieHellmanUtil.decodePublicKey(
                    Base64.getDecoder().decode(serverPublicKeyEncodedStr)
            );

            // 2) Extracts DH parameters from the server's public key
            DHParameterSpec params = ((DHPublicKey) serverPublicKey).getParams();

            // 3) Generates the client's key pair using the same parameters
            clientKeyPair = DiffieHellmanUtil.generateKeyPairWithParams(params);

            // 4) Sending the client's public key to the server
            byte[] clientPublicKeyEncoded = DiffieHellmanUtil.generatePublicKeyEncoded(clientKeyPair);
            out.println(Base64.getEncoder().encodeToString(clientPublicKeyEncoded));

            // 5) Calculates the shared secret key
            secretKey = DiffieHellmanUtil.agreeSecretKey(clientKeyPair.getPrivate(), serverPublicKey);
            byte[] hashedKey = DiffieHellmanUtil.hashSecretKey(secretKey);

            System.out.println("Secure channel established with server");

            // Thread used for reading the messages from the server (decrypt using AES)
            Thread readThread = new Thread(() -> {
                try {
                    String encryptedMsg;
                    while ((encryptedMsg = in.readLine()) != null) {
                        try {
                            String decrypted = AESUtil.decrypt(encryptedMsg, hashedKey);
                            System.out.println("[Server]: " + decrypted);
                        } catch (Exception e) {
                            System.err.println("Decryption error: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Connection closed: " + e.getMessage());
                }
            });
            readThread.start();

            // Sending the messages to the server (encrypt using AES)
            String userInput;
            while ((userInput = userIn.readLine()) != null) {
                try {
                    String encrypted = AESUtil.encrypt(userInput, hashedKey);
                    out.println(encrypted);
                } catch (Exception e) {
                    System.err.println("Encryption error: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
