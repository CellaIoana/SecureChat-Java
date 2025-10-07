package server_client_chat_app;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.KeyPair;

public class Server {

    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on Port " + PORT);

            KeyPair serverKeyPair = DiffieHellmanUtil.generateKeyPair();

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                   
                    ClientHandler clientThread = new ClientHandler(clientSocket, serverKeyPair);
                    clients.add(clientThread);
                    clientThread.start();
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error with server socket or DH key generation: " + e.getMessage());
        }
    }

    public static void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected: " + client.getSocket().getInetAddress());
    }
}
