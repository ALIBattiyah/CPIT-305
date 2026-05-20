package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class CustomerClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5050;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println(in.readLine());
            boolean running = true;
            while (running) {
                System.out.println("\n===== Keeta Eats Customer =====");
                System.out.println("1. List restaurants");
                System.out.println("2. View restaurant menu");
                System.out.println("3. Place order");
                System.out.println("4. Exit");
                System.out.print("Choose: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        sendAndPrint(out, in, "LIST_RESTAURANTS");
                        break;
                    case "2":
                        System.out.print("Enter restaurant id: ");
                        String restaurantId = scanner.nextLine();
                        sendAndPrint(out, in, "GET_MENU|" + restaurantId);
                        break;
                    case "3":
                        System.out.print("Your name: ");
                        String name = scanner.nextLine();
                        System.out.print("Your address: ");
                        String address = scanner.nextLine();
                        System.out.print("Phone: ");
                        String phone = scanner.nextLine();
                        System.out.print("Restaurant id: ");
                        String rid = scanner.nextLine();
                        System.out.print("Menu item id: ");
                        String itemId = scanner.nextLine();
                        System.out.print("Quantity: ");
                        String qty = scanner.nextLine();
                        String request = "PLACE_ORDER|" + name + "|" + address + "|" + phone + "|" + rid + "|" + itemId + "|" + qty;
                        sendAndPrint(out, in, request);
                        break;
                    case "4":
                        sendAndPrint(out, in, "EXIT");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (IOException e) {
            System.out.println("Customer client error: " + e.getMessage());
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
