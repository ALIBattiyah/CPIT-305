package database;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:derby:JuneEatsDB;create=true";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Derby driver not found. Add Java DB / Derby library in NetBeans.");
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            createTable(st, "CREATE TABLE RESTAURANTS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name VARCHAR(100), address VARCHAR(200))");
            createTable(st, "CREATE TABLE MENU_ITEMS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, restaurant_id INT, name VARCHAR(100), description VARCHAR(300), price DOUBLE, FOREIGN KEY (restaurant_id) REFERENCES RESTAURANTS(id))");
            createTable(st, "CREATE TABLE CUSTOMERS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name VARCHAR(100), address VARCHAR(200), phone VARCHAR(30))");
            createTable(st, "CREATE TABLE ORDERS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, customer_id INT, restaurant_id INT, order_time VARCHAR(40), status VARCHAR(50), total DOUBLE, FOREIGN KEY (customer_id) REFERENCES CUSTOMERS(id), FOREIGN KEY (restaurant_id) REFERENCES RESTAURANTS(id))");
            createTable(st, "CREATE TABLE ORDER_DETAILS (id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, order_id INT, menu_item_id INT, quantity INT, FOREIGN KEY (order_id) REFERENCES ORDERS(id), FOREIGN KEY (menu_item_id) REFERENCES MENU_ITEMS(id))");
        } catch (SQLException e) {
            System.out.println("Database initialization error: " + e.getMessage());
        }
    }

    private static void createTable(Statement st, String sql) {
        try { st.executeUpdate(sql); }
        catch (SQLException e) {
            if (!"X0Y32".equals(e.getSQLState())) {
                System.out.println("Create table warning: " + e.getMessage());
            }
        }
    }

    public static boolean isMenuEmpty() {
        String sql = "SELECT COUNT(*) FROM MENU_ITEMS";
        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            System.out.println("Menu check error: " + e.getMessage());
            return true;
        }
    }
}
