package util;

import database.DatabaseManager;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {
    private static final String LOG_PATH = "logs/orders.log";

    public static void loadMenuFromCsv(String csvPath) {
        File file = new File(csvPath);
        if (!file.exists()) {
            System.out.println("Menu CSV not found: " + csvPath);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                String restaurantName = parts[0].trim();
                String address = parts[1].trim();
                String itemName = parts[2].trim();
                String description = parts[3].trim();
                double price = Double.parseDouble(parts[4].trim());
                int restaurantId = findOrCreateRestaurant(restaurantName, address);
                insertMenuItem(restaurantId, itemName, description, price);
            }
            System.out.println("Menu loaded from CSV successfully.");
        } catch (IOException | SQLException e) {
            System.out.println("CSV loading error: " + e.getMessage());
        }
    }

    private static int findOrCreateRestaurant(String name, String address) throws SQLException {
        String findSql = "SELECT id FROM RESTAURANTS WHERE name=?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(findSql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }

        String insertSql = "INSERT INTO RESTAURANTS(name, address) VALUES(?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Could not create restaurant.");
    }

    private static void insertMenuItem(int restaurantId, String name, String description, double price) throws SQLException {
        String sql = "INSERT INTO MENU_ITEMS(restaurant_id, name, description, price) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restaurantId);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setDouble(4, price);
            ps.executeUpdate();
        }
    }

    public static synchronized void writeOrderLog(String message) {
        try {
            File logFile = new File(LOG_PATH);
            File parent = logFile.getParentFile();
            if (parent != null) parent.mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                bw.write("[" + time + "] " + message);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Log writing error: " + e.getMessage());
        }
    }
}
