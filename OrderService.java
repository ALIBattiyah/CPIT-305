package service;

import database.DatabaseManager;
import model.MenuItem;
import model.Order;
import model.Restaurant;
import util.FileManager;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderService {

    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT id, name, address FROM RESTAURANTS ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                restaurants.add(new Restaurant(rs.getInt("id"), rs.getString("name"), rs.getString("address")));
            }
        } catch (SQLException e) {
            System.out.println("Get restaurants error: " + e.getMessage());
        }
        return restaurants;
    }

    public List<MenuItem> getMenuByRestaurant(int restaurantId) {
        List<MenuItem> menu = new ArrayList<>();
        String sql = "SELECT id, restaurant_id, name, description, price FROM MENU_ITEMS WHERE restaurant_id=? ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    menu.add(new MenuItem(rs.getInt("id"), rs.getInt("restaurant_id"), rs.getString("name"), rs.getString("description"), rs.getDouble("price")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Get menu error: " + e.getMessage());
        }
        return menu;
    }

    public int createCustomer(String name, String address, String phone) {
        String sql = "INSERT INTO CUSTOMERS(name, address, phone) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, phone);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Create customer error: " + e.getMessage());
        }
        return -1;
    }

    public int placeOrder(String customerName, String address, String phone, int restaurantId, int menuItemId, int quantity) {
        if (quantity <= 0) return -1;
        double price = getMenuItemPrice(menuItemId, restaurantId);
        if (price < 0) return -1;

        int customerId = createCustomer(customerName, address, phone);
        if (customerId == -1) return -1;

        double total = price * quantity;
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String orderSql = "INSERT INTO ORDERS(customer_id, restaurant_id, order_time, status, total) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement orderPs = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderPs.setInt(1, customerId);
                orderPs.setInt(2, restaurantId);
                orderPs.setString(3, time);
                orderPs.setString(4, "Placed");
                orderPs.setDouble(5, total);
                orderPs.executeUpdate();
                int orderId;
                try (ResultSet keys = orderPs.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        return -1;
                    }
                    orderId = keys.getInt(1);
                }
                String detailSql = "INSERT INTO ORDER_DETAILS(order_id, menu_item_id, quantity) VALUES(?, ?, ?)";
                try (PreparedStatement detailPs = conn.prepareStatement(detailSql)) {
                    detailPs.setInt(1, orderId);
                    detailPs.setInt(2, menuItemId);
                    detailPs.setInt(3, quantity);
                    detailPs.executeUpdate();
                }
                conn.commit();
                FileManager.writeOrderLog("Order #" + orderId + " placed by " + customerName + " | Restaurant ID: " + restaurantId + " | Total: SAR " + total);
                return orderId;
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Place order transaction error: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Place order error: " + e.getMessage());
        }
        return -1;
    }

    private double getMenuItemPrice(int menuItemId, int restaurantId) {
        String sql = "SELECT price FROM MENU_ITEMS WHERE id=? AND restaurant_id=?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuItemId);
            ps.setInt(2, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("price");
            }
        } catch (SQLException e) {
            System.out.println("Get price error: " + e.getMessage());
        }
        return -1;
    }

    public List<Order> getOrdersByRestaurant(int restaurantId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT id, customer_id, restaurant_id, order_time, status, total FROM ORDERS WHERE restaurant_id=? ORDER BY id DESC";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(new Order(rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("restaurant_id"), rs.getString("order_time"), rs.getString("status"), rs.getDouble("total")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Get orders error: " + e.getMessage());
        }
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE ORDERS SET status=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            int updated = ps.executeUpdate();
            if (updated > 0) {
                FileManager.writeOrderLog("Order #" + orderId + " status updated to " + newStatus);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Update status error: " + e.getMessage());
        }
        return false;
    }
}
