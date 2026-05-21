package test;

import database.DatabaseManager;
import service.OrderService;
import util.FileManager;

public class KeetaEatsTestRunner {
    public static void main(String[] args) {
        System.out.println("Running KEETA Eats tests...");
        DatabaseManager.initializeDatabase();
        if (DatabaseManager.isMenuEmpty()) {
            FileManager.loadMenuFromCsv("C:\\Users\\ALI\\Documents\\NetBeansProjects\\FOODSYSTEM\\resources\\menus.csv");
        }

        OrderService service = new OrderService();

        assertTrue(service.getRestaurants().size() > 0, "Restaurants should be loaded from CSV into database");
        assertTrue(service.getMenuByRestaurant(1).size() > 0, "Restaurant 1 should have menu items");

        int orderId = service.placeOrder("Test Customer", "KAU", "0500000000", 1, 1, 2);
        assertTrue(orderId > 0, "Order should be created successfully");

        boolean updated = service.updateOrderStatus(orderId, "Delivered");
        assertTrue(updated, "Order status should be updated successfully");

        assertTrue(service.getOrdersByRestaurant(1).size() > 0, "Restaurant should have at least one order");

        System.out.println("All tests passed successfully.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Test failed: " + message);
        }
        System.out.println("PASSED: " + message);
    }
}
