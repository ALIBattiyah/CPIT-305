# Keeta Eats - CPIT-305 Final Project


- IO Streams: reading menus from CSV and writing order logs.
- Multithreading: server handles many clients concurrently using a thread pool.
- Networking: TCP socket communication between server, customer client, and restaurant client.
- Database Integration: JDBC CRUD operations using Java DB / Apache Derby.
- Exception Handling: try-with-resources and controlled error messages.
- Testing: simple test runner for service and file operations.

## Project Idea
Customers connect to a central server, browse restaurants and menu items, and place orders. Restaurants connect to the same server, view their orders, and update statuses such as Preparing, Out for Delivery, and Delivered.


## Demo Flow

1. Start FoodServer.
2. Start CustomerClient.
3. Choose option 1 to list restaurants.
4. Choose option 2 to view a menu.
5. Choose option 3 to place an order.
6. Start RestaurantClient.
7. Enter restaurant id, view orders, and update order status.
8. Check `logs/orders.log` to see file I/O output.

## Database Tables

- RESTAURANTS(id, name, address)
- MENU_ITEMS(id, restaurant_id, name, description, price)
- CUSTOMERS(id, name, address, phone)
- ORDERS(id, customer_id, restaurant_id, order_time, status, total)
- ORDER_DETAILS(id, order_id, menu_item_id, quantity)

## Team Presentation Points

- The server uses `ServerSocket` to accept client connections.
- Each connected client is handled by `ClientHandler` through an `ExecutorService` thread pool.
- The project uses JDBC PreparedStatements for database operations.
- Menus are loaded from a CSV file using BufferedReader.
- All important order events are written to a log file using BufferedWriter.
