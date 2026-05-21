package model;

public class Order {
    private int id;
    private int customerId;
    private int restaurantId;
    private String orderTime;
    private String status;
    private double total;

    public Order(int id, int customerId, int restaurantId, String orderTime, String status, double total) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.orderTime = orderTime;
        this.status = status;
        this.total = total;
    }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getRestaurantId() { return restaurantId; }
    public String getOrderTime() { return orderTime; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }

    @Override
    public String toString() {
        return "Order #" + id + " | Customer: " + customerId + " | Restaurant: " + restaurantId +
               " | Time: " + orderTime + " | Status: " + status + " | Total: SAR " + total;
    }
}
