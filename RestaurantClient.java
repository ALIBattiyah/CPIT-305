package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RestaurantClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5050;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println(in.readLine());
            System.out.print("Enter your restaurant id: ");
            String restaurantId = scanner.nextLine();

            boolean running = true;
            while (running) {
                System.out.println("\n===== Keeta Eats Restaurant =====");
                System.out.println("1. View my orders");
                System.out.println("2. Update order status");
                System.out.println("3. Exit");
                System.out.print("Choose: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        sendAndPrint(out, in, "VIEW_ORDERS|" + restaurantId);
                        break;
                    case "2":
                        System.out.print("Order id: ");
                        String orderId = scanner.nextLine();
                        System.out.println("Available statuses: Preparing, Out for Delivery, Delivered, Cancelled");
                        System.out.print("New status: ");
                        String status = scanner.nextLine();
                        sendAndPrint(out, in, "UPDATE_STATUS|" + orderId + "|" + status);
                        break;
                    case "3":
                        sendAndPrint(out, in, "EXIT");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (IOException e) {
            System.out.println("Restaurant client error: " + e.getMessage());
            System.out.println("Make sure FoodServer is running first.");
        }
    }

    private static void sendAndPrint(PrintWriter out, BufferedReader in, String request) throws IOException {
        out.println(request);
        String line;
        while ((line = in.readLine()) != null) {
            if ("END_RESPONSE".equals(line)) break;
            System.out.println(line);
        }
    }
}
