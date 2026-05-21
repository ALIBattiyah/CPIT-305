package model;

public class MenuItem {
    private int id;
    private int restaurantId;
    private String name;
    private String description;
    private double price;

    public MenuItem(int id, int restaurantId, String name, String description, double price) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public int getId() { return id; }
    public int getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return id + ". " + name + " - " + description + " - SAR " + price;
    }
}
