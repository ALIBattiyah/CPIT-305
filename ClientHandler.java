package server;

import model.MenuItem;
import model.Order;
import model.Restaurant;
import service.OrderService;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private OrderService service;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.service = new OrderService();
    }

    @Override
    public void run() {
        System.out.println("Client connected: " + socket.getInetAddress());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            out.println("WELCOME_TO_KEETA_EATS");
            String request;
            while ((request = in.readLine()) != null) {
                String response = processRequest(request);
                out.println(response);
                out.println("END_RESPONSE");
                if ("EXIT".equalsIgnoreCase(request.trim())) break;
            }
        } catch (IOException e) {
            System.out.println("Client handler error: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("Client disconnected.");
        }
    }

    private String processRequest(String request) {
        try {
            String[] parts = request.split("\\|", -1);
            String command = parts[0].trim().toUpperCase();

            if ("LIST_RESTAURANTS".equals(command)) {
                return listRestaurants();
            }
            if ("GET_MENU".equals(command)) {
                int restaurantId = Integer.parseInt(parts[1]);
                return getMenu(restaurantId);
            }
            if ("PLACE_ORDER".equals(command)) {
                String name = parts[1];
                String address = parts[2];
                String phone = parts[3];
                int restaurantId = Integer.parseInt(parts[4]);
                int menuItemId = Integer.parseInt(parts[5]);
                int quantity = Integer.parseInt(parts[6]);
                int orderId = service.placeOrder(name, address, phone, restaurantId, menuItemId, quantity);
                return orderId > 0 ? "SUCCESS: Order placed. Order ID = " + orderId : "ERROR: Could not place order.";
            }
            if ("VIEW_ORDERS".equals(command)) {
                int restaurantId = Integer.parseInt(parts[1]);
                return viewOrders(restaurantId);
            }
            if ("UPDATE_STATUS".equals(command)) {
                int orderId = Integer.parseInt(parts[1]);
                String status = parts[2];
                return service.updateOrderStatus(orderId, status) ? "SUCCESS: Order status updated." : "ERROR: Order not found.";
            }
            if ("EXIT".equals(command)) {
                return "Goodbye!";
            }
            return "ERROR: Unknown command.";
        } catch (Exception e) {
            return "ERROR: Invalid request format. " + e.getMessage();
        }
    }

    private String listRestaurants() {
        List<Restaurant> restaurants = service.getRestaurants();
        if (restaurants.isEmpty()) return "No restaurants found.";
        StringBuilder sb = new StringBuilder("Restaurants:\n");
        for (Restaurant r : restaurants) sb.append(r.toString()).append("\n");
        return sb.toString();
    }

    private String getMenu(int restaurantId) {
        List<MenuItem> menu = service.getMenuByRestaurant(restaurantId);
        if (menu.isEmpty()) return "No menu items found for this restaurant.";
        StringBuilder sb = new StringBuilder("Menu:\n");
        for (MenuItem item : menu) sb.append(item.toString()).append("\n");
        return sb.toString();
    }

    private String viewOrders(int restaurantId) {
        List<Order> orders = service.getOrdersByRestaurant(restaurantId);
        if (orders.isEmpty()) return "No orders found for this restaurant.";
        StringBuilder sb = new StringBuilder("Orders:\n");
        for (Order order : orders) sb.append(order.toString()).append("\n");
        return sb.toString();
    }
}
