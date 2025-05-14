package com.salesmanagementsys.model;

import java.sql.*;


import com.salesmanagementsys.model.Customer;
import com.salesmanagementsys.util.PasswordEncryptionUtil;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private Connection connection;
    private String dbURL;
    private String dbUser;
    private String dbPassword;

    public DatabaseManager() throws SQLException {
        // Load configuration before connecting
        loadConfiguration();
        
        // Now connect with the loaded configuration
        this.connection = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        initializeDatabase();
    }
    
    private void loadConfiguration() {
        // Default values (as fallback)
        this.dbURL = "jdbc:mysql://localhost:3306/amazingdessertbar";
        this.dbUser = "root";
        this.dbPassword = "Sequel@0101";
        
        try {
            // Try to load from current directory
            File configFile = new File("config.properties");
            
            // If not found, try to load from installation directory
            if (!configFile.exists()) {
                String appPath = System.getProperty("user.dir");
                configFile = new File(appPath, "config.properties");
                
                // If still not found, try app directory/resources
                if (!configFile.exists()) {
                    configFile = new File(new File(appPath, "app"), "config.properties");
                }
            }
            
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    Properties props = new Properties();
                    props.load(fis);
                    
                    this.dbURL = props.getProperty("db.url", this.dbURL);
                    this.dbUser = props.getProperty("db.user", this.dbUser);
                    this.dbPassword = props.getProperty("db.password", this.dbPassword);
                    
                    System.out.println("Database configuration loaded from: " + configFile.getAbsolutePath());
                }
            } else {
                System.out.println("config.properties not found, using default settings");
                // Create the config file with default values
                try (FileOutputStream fos = new FileOutputStream("config.properties")) {
                    Properties props = new Properties();
                    props.setProperty("db.url", this.dbURL);
                    props.setProperty("db.user", this.dbUser);
                    props.setProperty("db.password", this.dbPassword);
                    props.store(fos, "Amazing Dessert Bar Database Configuration");
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading/creating configuration: " + e.getMessage());
        }
    }
   

    private void initializeDatabase() throws SQLException {
        // Create tables if they don't exist
        createTables();
        
        // Add default items if items table is empty
        addDefaultItems();
    } 
    


    private void createTables() throws SQLException {
        // Create customers table
        String customerSql = "CREATE TABLE IF NOT EXISTS customers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "first_name VARCHAR(100), " +
                "last_name VARCHAR(100), " +
                "city VARCHAR(100), " +
                "mobile_number VARCHAR(20), " +
        		"debt DECIMAL(10, 2) DEFAULT 0.00)";  // Add this line
                
        // Create staff table
        String staffSql = "CREATE TABLE IF NOT EXISTS staff (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "first_name VARCHAR(100), " +
                "last_name VARCHAR(100), " +
                "hire_date VARCHAR(20), " +
                "job_role VARCHAR(50), " +
                "username VARCHAR(50), " +
                "password VARCHAR(50))";
        
     // In the createTables() method, add:
        String jobRoleSql = "CREATE TABLE IF NOT EXISTS job_roles (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(50) UNIQUE, " +
                "description VARCHAR(255))";        
                
        // Create items table
        String itemSql = "CREATE TABLE IF NOT EXISTS items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100), " +
                "price DECIMAL(10, 2))";
                
        // Create orders table
        String orderSql = "CREATE TABLE IF NOT EXISTS orders (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "customer_id INT, " +
                "staff_id INT, " +
                "order_date VARCHAR(20), " +
                "payment_method VARCHAR(50), " +
                "payment_status VARCHAR(20) DEFAULT 'Pending', " + 
                "total_amount DECIMAL(10, 2), " +
                "FOREIGN KEY (customer_id) REFERENCES customers(id), " +
                "FOREIGN KEY (staff_id) REFERENCES staff(id))";
                
        // Create preorders table
        String preorderSql = "CREATE TABLE IF NOT EXISTS preorders (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "customer_id INT, " +
                "staff_id INT, " +
                "preorder_date VARCHAR(20), " +
                "collection_date VARCHAR(20), " +
                "collection_time VARCHAR(20), " +
                "payment_status VARCHAR(20) DEFAULT 'Pending', " +  
                "total_amount DECIMAL(10, 2), " +
                "FOREIGN KEY (customer_id) REFERENCES customers(id), " +
                "FOREIGN KEY (staff_id) REFERENCES staff(id))";
                
        // Create order_items table
        String orderItemSql = "CREATE TABLE IF NOT EXISTS order_items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "order_id INT, " +
                "preorder_id INT, " +
                "item_id INT, " +
                "quantity INT, " +
                "FOREIGN KEY (order_id) REFERENCES orders(id), " +
                "FOREIGN KEY (preorder_id) REFERENCES preorders(id), " +
                "FOREIGN KEY (item_id) REFERENCES items(id))";
        // Create stock_purchases table
        String stockPurchaseSql = "CREATE TABLE IF NOT EXISTS stock_purchases (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "item_name VARCHAR(100), " +
                "price DECIMAL(10, 2), " +
                "quantity INT, " +
                "purchase_date VARCHAR(20), " +
                "notes TEXT)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(customerSql);
            stmt.execute(staffSql);
            stmt.execute(itemSql);
            stmt.execute(orderSql);
            stmt.execute(preorderSql);
            stmt.execute(orderItemSql);
            stmt.execute(stockPurchaseSql);
            stmt.execute(jobRoleSql);
        }
    }    
 
 
    public List<OrderItemDetail> getOrderItemsByOrderId(int orderId) throws SQLException {
        // Call the existing method with preOrderId set to 0 to only get items for this order
        return getOrderItemsWithDetails(orderId, 0);
    }

    public List<OrderItemDetail> getOrderItemsByPreOrderId(int preOrderId) throws SQLException {
        // Call the existing method with orderId set to 0 to only get items for this pre-order
        return getOrderItemsWithDetails(0, preOrderId);
    }


    private void addDefaultItems() throws SQLException {
        // Check if items table is empty
        String countSql = "SELECT COUNT(*) FROM items";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Add default items
                String insertSql = "INSERT INTO items (name, price) VALUES " +
                        "('Chocolate Cake', 5.99), " +
                        "('Vanilla Ice Cream', 3.49), " +
                        "('Fruit Tart', 4.99)";
                stmt.execute(insertSql);
            }
        }
        
        // Add default staff if table is empty
        countSql = "SELECT COUNT(*) FROM staff";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO staff (first_name, last_name, hire_date, job_role, username, password) " +
                        "VALUES ('Jane', 'Smith', '2025-01-01', 'manager', 'jane', 'password')";
                stmt.execute(insertSql);
            }
        }
    }

 // Make sure this method in DatabaseManager.java correctly inserts customer data with debt field
    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (first_name, last_name, city, mobile_number, debt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getCity());
            pstmt.setString(4, customer.getMobileNumber());
            pstmt.setDouble(5, 0.0); // Initialize debt to zero
            pstmt.executeUpdate();
        }
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("city"),
                    rs.getString("mobile_number"),
                    rs.getDouble("debt") // Added debt field
                ));
            }
        }
        return customers;
    }

    public void addStaff(Staff staff) throws SQLException {
        String sql = "INSERT INTO staff (first_name, last_name, hire_date, job_role, username, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, staff.getFirstName());
            pstmt.setString(2, staff.getLastName());
            pstmt.setString(3, staff.getHireDate());
            pstmt.setString(4, staff.getJobRole());
            pstmt.setString(5, staff.getUsername());
            pstmt.setString(6, staff.getPassword());
            pstmt.executeUpdate();
        }
    }
    
    // Add this to the initializeDatabase() method
    private void addDefaultRoles() throws SQLException {
        // Check if job_roles table is empty
        String countSql = "SELECT COUNT(*) FROM job_roles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Add default roles
                String insertSql = "INSERT INTO job_roles (name, description) VALUES " +
                        "('Admin', 'Administrator with full system access'), " +
                        "('Manager', 'Store manager with most access rights'), " +
                        "('Sales Officer', 'Sales staff with reporting access'), " +
                        "('Cashier', 'Staff handling transactions'), " +
                        "('Server', 'Staff serving customers')";
                stmt.execute(insertSql);
            }
        }
    }

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                staffList.add(new Staff(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("hire_date"),
                    rs.getString("job_role"),
                    rs.getString("username"),
                    rs.getString("password")
                ));
            }
        }
        return staffList;
    } 
    
 // Add methods to retrieve job roles
    public List<JobRole> getAllJobRoles() throws SQLException {
        List<JobRole> roles = new ArrayList<>();
        String sql = "SELECT * FROM job_roles ORDER BY name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new JobRole(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        }
        return roles;
    }


    public List<Item> getAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new Item(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                ));
            }
        }
        return items;
    }

    public int addOrder(Order order, List<OrderItem> orderItems) throws SQLException {
        connection.setAutoCommit(false);
        int orderId = 0;
        
        try {
            String sql = "INSERT INTO orders (customer_id, staff_id, order_date, payment_method, total_amount) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, order.getCustomerId());
                pstmt.setInt(2, order.getStaffId());
                pstmt.setString(3, order.getOrderDate());
                pstmt.setString(4, order.getPaymentMethod());
                pstmt.setDouble(5, order.getTotalAmount());
                pstmt.executeUpdate();
                
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }
            }
            
            String itemSql = "INSERT INTO order_items (order_id, preorder_id, item_id, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(itemSql)) {
                for (OrderItem item : orderItems) {
                    pstmt.setInt(1, orderId);
                    pstmt.setNull(2, Types.INTEGER);
                    pstmt.setInt(3, item.getItemId());
                    pstmt.setInt(4, item.getQuantity());
                    pstmt.executeUpdate();
                }
            }
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
        
        return orderId;
    }
    
 // Add these methods to DatabaseManager.java

    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(new Order(
                    rs.getInt("id"),
                    rs.getInt("customer_id"),
                    rs.getInt("staff_id"),
                    rs.getString("order_date"),
                    rs.getString("payment_method"),
                    rs.getDouble("total_amount"),
                    rs.getString("payment_status") // Added payment status
                ));
            }
        }
        return orders;
    }
    
    public void updateOrder(Order order, List<OrderItem> orderItems) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Update the order
            String updateOrderSQL = "UPDATE orders SET customer_id = ?, staff_id = ?, order_date = ?, " +
                                   "payment_method = ?, total_amount = ?, payment_status = ? WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateOrderSQL)) {
                pstmt.setInt(1, order.getCustomerId());
                pstmt.setInt(2, order.getStaffId());
                pstmt.setString(3, order.getOrderDate());
                pstmt.setString(4, order.getPaymentMethod());
                pstmt.setDouble(5, order.getTotalAmount());
                pstmt.setString(6, order.getPaymentStatus());
                pstmt.setInt(7, order.getId());
                pstmt.executeUpdate();
            }
            
            // Delete existing order items
            String deleteItemsSQL = "DELETE FROM order_items WHERE order_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteItemsSQL)) {
                pstmt.setInt(1, order.getId());
                pstmt.executeUpdate();
            }
            
            // Insert new order items
            String insertItemSQL = "INSERT INTO order_items (order_id, preorder_id, item_id, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertItemSQL)) {
                for (OrderItem item : orderItems) {
                    pstmt.setInt(1, order.getId());
                    pstmt.setNull(2, Types.INTEGER);
                    pstmt.setInt(3, item.getItemId());
                    pstmt.setInt(4, item.getQuantity());
                    pstmt.executeUpdate();
                }
            }
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public void deleteOrder(int orderId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // First delete associated order items
            String deleteItemsSQL = "DELETE FROM order_items WHERE order_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteItemsSQL)) {
                pstmt.setInt(1, orderId);
                pstmt.executeUpdate();
            }
            
            // Then delete the order itself
            String deleteOrderSQL = "DELETE FROM orders WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteOrderSQL)) {
                pstmt.setInt(1, orderId);
                pstmt.executeUpdate();
            }
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public int addPreOrder(PreOrder preOrder, List<OrderItem> orderItems) throws SQLException {
        connection.setAutoCommit(false);
        int preorderId = 0;
        
        try {
            String sql = "INSERT INTO preorders (customer_id, staff_id, preorder_date, collection_date, collection_time, total_amount) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, preOrder.getCustomerId());
                pstmt.setInt(2, preOrder.getStaffId());
                pstmt.setString(3, preOrder.getPreorderDate());
                pstmt.setString(4, preOrder.getCollectionDate());
                pstmt.setString(5, preOrder.getCollectionTime());
                pstmt.setDouble(6, preOrder.getTotalAmount());
                pstmt.executeUpdate();
                
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        preorderId = rs.getInt(1);
                    }
                }
            }
            
            String itemSql = "INSERT INTO order_items (order_id, preorder_id, item_id, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(itemSql)) {
                for (OrderItem item : orderItems) {
                    pstmt.setNull(1, Types.INTEGER);
                    pstmt.setInt(2, preorderId);
                    pstmt.setInt(3, item.getItemId());
                    pstmt.setInt(4, item.getQuantity());
                    pstmt.executeUpdate();
                }
            }
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
        
        return preorderId;
    }
    
    public List<PreOrder> getAllPreOrders() throws SQLException {
        List<PreOrder> preOrders = new ArrayList<>();
        String sql = "SELECT * FROM preorders";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                preOrders.add(new PreOrder(
                    rs.getInt("id"),
                    rs.getInt("customer_id"),
                    rs.getInt("staff_id"),
                    rs.getString("preorder_date"),
                    rs.getString("collection_date"),
                    rs.getString("collection_time"),
                    rs.getDouble("total_amount"),
                    rs.getString("payment_status") // Added payment status
                ));
            }
        }
        return preOrders;
    }
    
    public void updatePreOrder(PreOrder preOrder, List<OrderItem> orderItems) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Update the preorder
            String updatePreOrderSQL = "UPDATE preorders SET customer_id = ?, staff_id = ?, preorder_date = ?, " +
                                      "collection_date = ?, collection_time = ?, total_amount = ?, payment_status = ? " +
                                      "WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updatePreOrderSQL)) {
                pstmt.setInt(1, preOrder.getCustomerId());
                pstmt.setInt(2, preOrder.getStaffId());
                pstmt.setString(3, preOrder.getPreorderDate());
                pstmt.setString(4, preOrder.getCollectionDate());
                pstmt.setString(5, preOrder.getCollectionTime());
                pstmt.setDouble(6, preOrder.getTotalAmount());
                pstmt.setString(7, preOrder.getPaymentStatus());
                pstmt.setInt(8, preOrder.getId());
                pstmt.executeUpdate();
            }
            
            // Delete existing order items
            String deleteItemsSQL = "DELETE FROM order_items WHERE preorder_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteItemsSQL)) {
                pstmt.setInt(1, preOrder.getId());
                pstmt.executeUpdate();
            }
            
            // Insert new order items
            String insertItemSQL = "INSERT INTO order_items (order_id, preorder_id, item_id, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertItemSQL)) {
                for (OrderItem item : orderItems) {
                    pstmt.setNull(1, Types.INTEGER);
                    pstmt.setInt(2, preOrder.getId());
                    pstmt.setInt(3, item.getItemId());
                    pstmt.setInt(4, item.getQuantity());
                    pstmt.executeUpdate();
                }
            }
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }            
     
    
    public void deletePreOrder(int preOrderId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // First delete associated order items
            String deleteItemsSQL = "DELETE FROM order_items WHERE preorder_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteItemsSQL)) {
                pstmt.setInt(1, preOrderId);
                pstmt.executeUpdate();
            }
            
            // Then delete the preorder itself
            String deletePreOrderSQL = "DELETE FROM preorders WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deletePreOrderSQL)) {
                pstmt.setInt(1, preOrderId);
                pstmt.executeUpdate();
            }
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    
    
    public void addItem(Item item) throws SQLException {
        String sql = "INSERT INTO items (name, price) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, item.getName());
            pstmt.setDouble(2, item.getPrice());
            pstmt.executeUpdate();
        }
    }
    
 // Add these new methods to DatabaseManager:
    public void addStockPurchase(StockPurchase purchase) throws SQLException {
        String sql = "INSERT INTO stock_purchases (item_name, price, quantity, purchase_date, notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, purchase.getItemName());
            pstmt.setDouble(2, purchase.getPrice());
            pstmt.setInt(3, purchase.getQuantity());
            pstmt.setString(4, purchase.getPurchaseDate());
            pstmt.setString(5, purchase.getNotes());
            pstmt.executeUpdate();
        }
    }
    
    public List<StockPurchase> getAllStockPurchases() throws SQLException {
        List<StockPurchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM stock_purchases ORDER BY purchase_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                purchases.add(new StockPurchase(
                    rs.getInt("id"),
                    rs.getString("item_name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("purchase_date"),
                    rs.getString("notes")
                ));
            }
        }
        return purchases;
    }
    
    public void updateOrderPaymentStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET payment_status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        }
    }

    public void updatePreOrderPaymentStatus(int preOrderId, String status) throws SQLException {
        String sql = "UPDATE preorders SET payment_status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, preOrderId);
            pstmt.executeUpdate();
        }
    }
    
    public void updateCustomerDebt(int customerId, double amount) throws SQLException {
		String sql = "UPDATE customers SET debt = debt + ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setDouble(1, amount);
			pstmt.setInt(2, customerId);
			pstmt.executeUpdate();
		}
	}
    
 // Add to DatabaseManager.java
    public Order getOrderById(int orderId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("staff_id"),
                        rs.getString("order_date"),
                        rs.getString("payment_method"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_status")
                    );
                }
            }
        }
        return null;
    }

    public PreOrder getPreOrderById(int preOrderId) throws SQLException {
        String sql = "SELECT * FROM preorders WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, preOrderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new PreOrder(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("staff_id"),
                        rs.getString("preorder_date"),
                        rs.getString("collection_date"),
                        rs.getString("collection_time"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_status")
                    );
                }
            }
        }
        return null;
    }


    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    /*    
    public Staff authenticateStaff(String username, String password) throws SQLException {
        String sql = "SELECT * FROM staff WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Staff(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("hire_date"),
                        rs.getString("job_role"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        }
        return null;
    }*/
    
    public Staff getStaffByUsername(String username) throws SQLException {
        String query = "SELECT * FROM staff WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Staff staff = new Staff();
                    staff.setId(rs.getInt("id"));
                    staff.setFirstName(rs.getString("first_name"));
                    staff.setLastName(rs.getString("last_name"));
                    staff.setHireDate(rs.getString("hire_date"));
                    staff.setJobRole(rs.getString("job_role"));
                    staff.setUsername(rs.getString("username"));
                    String password = rs.getString("password");                   
                    staff.setPassword(password);
                    return staff;
                }
            }
        }
        return null;
    }

    
    public List<StockPurchase> getStockPurchasesByDateRange(String startDate, String endDate) throws SQLException {
        List<StockPurchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM stock_purchases WHERE purchase_date BETWEEN ? AND ? ORDER BY purchase_date";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    purchases.add(new StockPurchase(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("purchase_date"),
                        rs.getString("notes")
                    ));
                }
            }
        }
        return purchases;
    }

    public List<Order> getOrdersByDateRange(String startDate, String endDate) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE order_date BETWEEN ? AND ? ORDER BY order_date";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("staff_id"),
                        rs.getString("order_date"),
                        rs.getString("payment_method"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_status")
                    ));
                }
            }
        }
        return orders;
    }

    public List<PreOrder> getPreOrdersByDateRange(String startDate, String endDate) throws SQLException {
        List<PreOrder> preOrders = new ArrayList<>();
        String sql = "SELECT * FROM preorders WHERE preorder_date BETWEEN ? AND ? ORDER BY preorder_date";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    preOrders.add(new PreOrder(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("staff_id"),
                        rs.getString("preorder_date"),
                        rs.getString("collection_date"),
                        rs.getString("collection_time"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_status")
                    ));
                }
            }
        }
        return preOrders;
    }

    public List<OrderItemDetail> getOrderItemsWithDetails(int orderId, int preOrderId) throws SQLException {
        List<OrderItemDetail> itemDetails = new ArrayList<>();
        
        // SQL joins order_items with items to get name and price
        String sql = "SELECT oi.*, i.name as item_name, i.price FROM order_items oi " +
                     "JOIN items i ON oi.item_id = i.id " +
                     "WHERE oi.order_id = ? OR oi.preorder_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, preOrderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itemDetails.add(new OrderItemDetail(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                    ));
                }
            }
        }
        return itemDetails;
    }    
 
    public boolean customerNameExists(String firstName, String lastName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE first_name = ? AND last_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean customerMobileExists(String mobileNumber) throws SQLException {
        if (mobileNumber == null || mobileNumber.isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM customers WHERE mobile_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, mobileNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean staffUsernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM staff WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    // Helper class for order item details with full information
    public static class OrderItemDetail {
        private int itemId;
        private String itemName;
        private int quantity;
        private double price;
        
        public OrderItemDetail(int itemId, String itemName, int quantity, double price) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
        }
        
        public int getItemId() { return itemId; }
        public String getItemName() { return itemName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public double getTotalPrice() { return quantity * price; }
    }
    
    
    
    public Staff getStaffById(int staffId) throws SQLException {
        String sql = "SELECT * FROM staff WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Staff staff = new Staff();
                    staff.setId(rs.getInt("id"));
                    staff.setFirstName(rs.getString("first_name"));
                    staff.setLastName(rs.getString("last_name"));
                    staff.setHireDate(rs.getString("hire_date"));
                    staff.setJobRole(rs.getString("job_role"));
                    staff.setUsername(rs.getString("username"));
                    staff.setPassword(rs.getString("password"));
                    return staff;
                }
            }
        }
        return null;
    }
    
    public void updateStaff(Staff staff) throws SQLException {
        String sql = "UPDATE staff SET first_name = ?, last_name = ?, hire_date = ?, " +
                    "job_role = ?, username = ?, password = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, staff.getFirstName());
            pstmt.setString(2, staff.getLastName());
            pstmt.setString(3, staff.getHireDate());
            pstmt.setString(4, staff.getJobRole());
            pstmt.setString(5, staff.getUsername());
            pstmt.setString(6, staff.getPassword());
            pstmt.setInt(7, staff.getId());
            pstmt.executeUpdate();
        }
    }
    
    public List<Order> searchOrders(String customerName, String fromDate, String toDate) throws SQLException {
        List<Order> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT o.* FROM orders o");
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;
        
        // Join with customers if we're searching by customer name
        if (customerName != null && !customerName.isEmpty()) {
            sql.append(" JOIN customers c ON o.customer_id = c.id");
            sql.append(" WHERE (c.first_name LIKE ? OR c.last_name LIKE ?)");
            params.add("%" + customerName + "%");
            params.add("%" + customerName + "%");
            hasWhere = true;
        }
        
        // Add date range criteria
        if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
            if (hasWhere) {
                sql.append(" AND");
            } else {
                sql.append(" WHERE");
                hasWhere = true;
            }
            sql.append(" o.order_date BETWEEN ? AND ?");
            params.add(fromDate);
            params.add(toDate);
        }
        
        // Order by date
        sql.append(" ORDER BY o.order_date DESC");
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("staff_id"),
                        rs.getString("order_date"),
                        rs.getString("payment_method"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_status")
                    ));
                }
            }
        }
        return orders;
    }
    
    public List<PreOrder> searchPreOrders(String customerName, String fromDate, String toDate) throws SQLException {
        List<PreOrder> preOrders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.* FROM preorders p");
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;
        
        // Join with customers if we're searching by customer name
        if (customerName != null && !customerName.isEmpty()) {
            sql.append(" JOIN customers c ON p.customer_id = c.id");
            sql.append(" WHERE (c.first_name LIKE ? OR c.last_name LIKE ?)");
            params.add("%" + customerName + "%");
            params.add("%" + customerName + "%");
            hasWhere = true;
        }
        
        // Add date range criteria
        if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
            if (hasWhere) {
                sql.append(" AND");
            } else {
                sql.append(" WHERE");
                hasWhere = true;
            }
            sql.append(" (p.preorder_date BETWEEN ? AND ? OR p.collection_date BETWEEN ? AND ?)");
            params.add(fromDate);
            params.add(toDate);
            params.add(fromDate);
            params.add(toDate);
        }
        
        // Order by date
        sql.append(" ORDER BY p.preorder_date DESC");
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    preOrders.add(new PreOrder(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("staff_id"),
                        rs.getString("preorder_date"),
                        rs.getString("collection_date"),
                        rs.getString("collection_time"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_status")
                    ));
                }
            }
        }
        return preOrders;
    }
    
 // Add to DatabaseManager.java
    public void createTablesTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS tables (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "number TEXT NOT NULL, " +
                    "capacity INTEGER NOT NULL, " +
                    "status TEXT DEFAULT 'Available' CHECK(status IN ('Available', 'Occupied', 'Reserved')), " +
                    "current_order_id INTEGER DEFAULT NULL, " +
                    "current_preorder_id INTEGER DEFAULT NULL)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public int addTable(Table table) throws SQLException {
        String sql = "INSERT INTO tables (number, capacity, status) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, table.getNumber());
            pstmt.setInt(2, table.getCapacity());
            pstmt.setString(3, table.getStatus());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Table> getAllTables() throws SQLException {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT * FROM tables ORDER BY number";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Table table = new Table(
                    rs.getInt("id"),
                    rs.getString("number"),
                    rs.getInt("capacity"),
                    rs.getString("status")
                );
                table.setCurrentOrderId(rs.getObject("current_order_id", Integer.class));
                table.setCurrentPreOrderId(rs.getObject("current_preorder_id", Integer.class));
                tables.add(table);
            }
        }
        return tables;
    }

    public List<Table> getAvailableTables() throws SQLException {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT * FROM tables WHERE status = 'Available' ORDER BY number";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tables.add(new Table(
                    rs.getInt("id"),
                    rs.getString("number"),
                    rs.getInt("capacity"),
                    rs.getString("status")
                ));
            }
        }
        return tables;
    }

    public void assignTableToOrder(int tableId, int orderId) throws SQLException {
        // First, update the table status
        String tableSql = "UPDATE tables SET status = 'Occupied', current_order_id = ?, current_preorder_id = NULL WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(tableSql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, tableId);
            pstmt.executeUpdate();
        }
    }

    public void assignTableToPreOrder(int tableId, int preOrderId) throws SQLException {
        // First, update the table status
        String tableSql = "UPDATE tables SET status = 'Occupied', current_preorder_id = ?, current_order_id = NULL WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(tableSql)) {
            pstmt.setInt(1, preOrderId);
            pstmt.setInt(2, tableId);
            pstmt.executeUpdate();
        }
    }

    public void releaseTable(int tableId) throws SQLException {
        String sql = "UPDATE tables SET status = 'Available', current_order_id = NULL, current_preorder_id = NULL WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, tableId);
            pstmt.executeUpdate();
        }
    }

    public Table getTableById(int tableId) throws SQLException {
        String sql = "SELECT * FROM tables WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, tableId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Table table = new Table(
                        rs.getInt("id"),
                        rs.getString("number"),
                        rs.getInt("capacity"),
                        rs.getString("status")
                    );
                    table.setCurrentOrderId(rs.getObject("current_order_id", Integer.class));
                    table.setCurrentPreOrderId(rs.getObject("current_preorder_id", Integer.class));
                    return table;
                }
            }
        }
        return null;
    }

    public Table getTableForOrder(int orderId) throws SQLException {
        String sql = "SELECT * FROM tables WHERE current_order_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Table table = new Table(
                        rs.getInt("id"),
                        rs.getString("number"),
                        rs.getInt("capacity"),
                        rs.getString("status")
                    );
                    table.setCurrentOrderId(rs.getObject("current_order_id", Integer.class));
                    table.setCurrentPreOrderId(rs.getObject("current_preorder_id", Integer.class));
                    return table;
                }
            }
        }
        return null;
    }

    public Table getTableForPreOrder(int preOrderId) throws SQLException {
        String sql = "SELECT * FROM tables WHERE current_preorder_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, preOrderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Table table = new Table(
                        rs.getInt("id"),
                        rs.getString("number"),
                        rs.getInt("capacity"),
                        rs.getString("status")
                    );
                    table.setCurrentOrderId(rs.getObject("current_order_id", Integer.class));
                    table.setCurrentPreOrderId(rs.getObject("current_preorder_id", Integer.class));
                    return table;
                }
            }
        }
        return null;
    }
    
    public List<Object[]> getTablesWithOrderDetails() throws SQLException {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT t.id, t.number, t.capacity, t.status, " +
                     "t.current_order_id, t.current_preorder_id, " +
                     "o.id as order_id, c1.first_name as order_customer_first, c1.last_name as order_customer_last, " +
                     "p.id as preorder_id, c2.first_name as preorder_customer_first, c2.last_name as preorder_customer_last, " +
                     "o.total_amount as order_amount, p.total_amount as preorder_amount " +
                     "FROM tables t " +
                     "LEFT JOIN orders o ON t.current_order_id = o.id " +
                     "LEFT JOIN preorders p ON t.current_preorder_id = p.id " +
                     "LEFT JOIN customers c1 ON o.customer_id = c1.id " +
                     "LEFT JOIN customers c2 ON p.customer_id = c2.id " +
                     "ORDER BY t.number";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Table table = new Table(
                    rs.getInt("id"),
                    rs.getString("number"),
                    rs.getInt("capacity"),
                    rs.getString("status")
                );
                
                int orderId = rs.getInt("order_id");
                String orderDetails = null;
                if (!rs.wasNull()) {
                    String customerName = rs.getString("order_customer_first") + " " + rs.getString("order_customer_last");
                    double amount = rs.getDouble("order_amount");
                    orderDetails = "Order #" + orderId + " - " + customerName + " ($" + String.format("%.2f", amount) + ")";
                }
                
                int preorderId = rs.getInt("preorder_id");
                String preorderDetails = null;
                if (!rs.wasNull()) {
                    String customerName = rs.getString("preorder_customer_first") + " " + rs.getString("preorder_customer_last");
                    double amount = rs.getDouble("preorder_amount");
                    preorderDetails = "Pre-Order #" + preorderId + " - " + customerName + " ($" + String.format("%.2f", amount) + ")";
                }
                
                if (orderId == 0) table.setCurrentOrderId(null);
                else table.setCurrentOrderId(orderId);
                
                if (preorderId == 0) table.setCurrentPreOrderId(null);
                else table.setCurrentPreOrderId(preorderId);
                
                result.add(new Object[] { table, orderDetails, preorderDetails });
            }
        }
        
        return result;
    }
    
 }





