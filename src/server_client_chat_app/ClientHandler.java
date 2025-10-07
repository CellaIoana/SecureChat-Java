package server_client_chat_app;

import java.io.*;
import java.net.*;
import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.Base64;
import java.security.KeyPair;

public class ClientHandler extends Thread {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private KeyPair serverKeyPair;
    private SecretKey secretKey;

    public ClientHandler(Socket socket, KeyPair serverKeyPair) {
        this.socket = socket;
        this.serverKeyPair = serverKeyPair;
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            byte[] serverPublicKeyEncoded = DiffieHellmanUtil.generatePublicKeyEncoded(serverKeyPair);
            out.println(Base64.getEncoder().encodeToString(serverPublicKeyEncoded));

            String clientPublicKeyEncodedStr = in.readLine();
            PublicKey clientPublicKey = DiffieHellmanUtil.decodePublicKey(Base64.getDecoder().decode(clientPublicKeyEncodedStr));

            secretKey = DiffieHellmanUtil.agreeSecretKey(serverKeyPair.getPrivate(), clientPublicKey);
            byte[] hashedKey = DiffieHellmanUtil.hashSecretKey(secretKey);

            while (true) {
            	
                String encryptedMessage = in.readLine();
                if (encryptedMessage == null) 
                	break;
                  
                try {
                    String decryptedMessage = AESUtil.decrypt(encryptedMessage, hashedKey);
                    Server.broadcastMessage(decryptedMessage);
                } catch (Exception e) {
                    System.err.println("Decryption error: " + e.getMessage());
                    break;
                }
                
            }
        } catch (Exception e) {
            System.err.println("Error in client handler: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
            Server.removeClient(this);
        }
    }

    public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public KeyPair getServerKeyPair() {
		return serverKeyPair;
	}

	public void setServerKeyPair(KeyPair serverKeyPair) {
		this.serverKeyPair = serverKeyPair;
	}

	public SecretKey getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}

	public void sendMessage(String message) {
        out.println(message);
    }
}
