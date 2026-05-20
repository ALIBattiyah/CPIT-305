package server;

import database.DatabaseManager;
import util.FileManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodServer {
    public static final int PORT = 5050;
    private static final int MAX_CLIENTS = 10;

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        if (DatabaseManager.isMenuEmpty()) {
            FileManager.loadMenuFromCsv("resources/menus.csv");
        }

        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
        System.out.println("KEETA Eats Server started on port " + PORT);
        System.out.println("Waiting for customers and restaurants...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                System.out.println("Connect successfully");
                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
