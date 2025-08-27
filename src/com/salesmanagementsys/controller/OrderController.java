package com.salesmanagementsys.controller;

import com.salesmanagementsys.model.DatabaseManager;


import com.salesmanagementsys.model.Order;
import com.salesmanagementsys.model.OrderItem;
import com.salesmanagementsys.model.Item; // Add this import
import com.salesmanagementsys.view.OrderView;
import com.salesmanagementsys.view.OrderListView;
import com.salesmanagementsys.view.TableAssignmentDialog;
import java.util.ArrayList;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.eclipse.swt.SWT;
import com.salesmanagementsys.model.Table;
import java.util.List; // Use this instead of org.eclipse.swt.widgets.List


public class OrderController {
    private OrderView view;
    private OrderListView listView;
    private DatabaseManager dbManager;
    
    public OrderController(OrderView view, DatabaseManager dbManager) {
        this.view = view;
        this.dbManager = dbManager;
    }
    
    public void setView(OrderView view) {
        this.view = view;
    }
    
    public void setListView(OrderListView listView) {
        this.listView = listView;
    }
    
    // Method to handle adding an item to the order list
    // This method is called when the user clicks the "Add Item" button
    public void addItemToOrder(String itemText, String quantityText, org.eclipse.swt.widgets.List orderItemsList) {
        if (itemText == null || itemText.isEmpty()) {
            view.showMessage("Please select an item", SWT.ICON_ERROR);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                view.showMessage("Quantity must be greater than zero", SWT.ICON_ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            view.showMessage("Please enter a valid quantity", SWT.ICON_ERROR);
            return;
        }

        // Add item to the UI list
        orderItemsList.add(quantity + "x " + itemText);
    }

    
    // Method to process the order placement
    public void placeOrder(String customerText, String staffText, String paymentMethod, String[] orderItems) {
        if (customerText == null || customerText.isEmpty() || customerText.equals("None")) {
            view.showMessage("Please select a customer", org.eclipse.swt.SWT.ICON_ERROR);
            return;
        }
        
        if (staffText == null || staffText.isEmpty()) {
            view.showMessage("Please select a staff member", org.eclipse.swt.SWT.ICON_ERROR);
            return;
        }
        
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            view.showMessage("Please select a payment method", org.eclipse.swt.SWT.ICON_ERROR);
            return;
        }
        
        if (orderItems.length == 0) {
            view.showMessage("Please add at least one item to the order", org.eclipse.swt.SWT.ICON_ERROR);
            return;
        }
        
        try {
            // Extract IDs from the selected items
            int customerId = Integer.parseInt(customerText.split(" - ")[0]);
            int staffId = Integer.parseInt(staffText.split(" - ")[0]);
            
            // Calculate total amount properly
            double totalAmount = 0.0;
            java.util.List<OrderItem> itemsList = new ArrayList<>();
            
            for (String itemString : orderItems) {
                // Format is "quantityx Item Name"
                String[] parts = itemString.split("x ", 2);
                int quantity = Integer.parseInt(parts[0]);
                
                // Get item ID and price from item name
                String itemName = parts[1];
                Item item = findItemByName(itemName);
                
                if (item != null) {
                    totalAmount += (item.getPrice() * quantity);
                    itemsList.add(new OrderItem(0, 0, 0, item.getId(), quantity));
                }
            }
            
            // Get current date
            String orderDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            // Create and save the order with correct items
            createOrder(customerId, staffId, orderDate, paymentMethod, totalAmount, itemsList);
            
            // Clear the order form
            view.clearOrder();
            
        } catch (Exception e) {
            view.showMessage("Error processing order: " + e.getMessage(), org.eclipse.swt.SWT.ICON_ERROR);
        }
    }
    
    private Item findItemByName(String displayText) throws SQLException {
        if (displayText == null || displayText.trim().isEmpty()) {
            return null;
        }
        
        // Remove any currency symbols and formatting
        // This assumes item format might be like "Item Name - $2.99" or just "Item Name"
        String cleanedItemName = displayText.trim().split(" - ")[0].trim();
        
        // For debugging
        System.out.println("Looking for item with cleaned name: '" + cleanedItemName + "'");
        
        java.util.List<Item> items = dbManager.getAllItems();
        for (Item item : items) {
            System.out.println("Comparing with DB item: '" + item.getName() + "'");
            
            // Try exact match first
            if (item.getName().equals(cleanedItemName)) {
                System.out.println("Found exact match: " + item.getName() + " - " + item.getPrice());
                return item;
            }
        }
        
        System.out.println("No matching item found for: " + cleanedItemName);
        return null;
    }

    
 // createOrder that accepts itemsList
    public void createOrder(int customerId, int staffId, String orderDate, String paymentMethod, 
                           double totalAmount, java.util.List<OrderItem> orderItems) {
        try {
            // Create Order object
            Order order = new Order(0, customerId, staffId, orderDate, paymentMethod, totalAmount, "Pending");
            
            int orderId = dbManager.addOrder(order, orderItems);
            
            // Add to customer's debt
            dbManager.updateCustomerDebt(customerId, totalAmount);
            
            if (view != null) {
                view.showMessage("Order created successfully", org.eclipse.swt.SWT.ICON_INFORMATION);
            }
            refreshOrderList();
        } catch (SQLException e) {
            if (view != null) {
                view.showMessage("Error creating order: " + e.getMessage(), org.eclipse.swt.SWT.ICON_ERROR);
            }
        }
    }

    public void updateOrderPaymentStatus(int orderId, String status) {
        try {
            // Get the order details to access customer ID and amount
            Order order = dbManager.getOrderById(orderId);
            
            if (order != null && "Paid".equals(status)) {
                // Reduce the debt by the order amount for ALL orders (not just Credit)
                dbManager.updateCustomerDebt(order.getCustomerId(), -order.getTotalAmount());
            }
            
            dbManager.updateOrderPaymentStatus(orderId, status);
            refreshOrderList();
        } catch (SQLException e) {
            if (listView != null) {
                listView.showMessage("Error updating payment status: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }
    
 // Delete Order, Call the dbManager.deleteOrder
    public void deleteOrder(int orderId) {
        try {
            // Get the order to access customer info for debt adjustment
            Order order = dbManager.getOrderById(orderId);
            
            if (order != null) {
                if ("Paid".equals(order.getPaymentStatus())) {
                    // If the order was paid, adjust the debt back
                    dbManager.updateCustomerDebt(order.getCustomerId(), order.getTotalAmount());
                } else {
                    // If order wasn't paid, remove the debt
                    dbManager.updateCustomerDebt(order.getCustomerId(), -order.getTotalAmount());
                }
                
                dbManager.deleteOrder(orderId);
                refreshOrderList();
                if (listView != null) {
                    listView.showMessage("Order deleted successfully", SWT.ICON_INFORMATION);
                }
            }
        } catch (SQLException e) {
            if (listView != null) {
                listView.showMessage("Error deleting order: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }

    public void showOrderDetails(int orderId) {
        try {
            // Use fully qualified name for java.util.List
            java.util.List<DatabaseManager.OrderItemDetail> items = dbManager.getOrderItemsByOrderId(orderId);
            if (listView != null) {
                listView.displayOrderDetails(items);
            }
        } catch (SQLException e) {
            if (listView != null) {
                listView.showMessage("Error retrieving order details: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }
 
    public void refreshOrderList() {
        if (listView != null) {
            try {
                java.util.List<Order> orders = dbManager.getAllOrders();
                // Load the latest customer data before displaying orders
                listView.updateCustomerData(dbManager.getAllCustomers());
                listView.displayOrders(orders);
            } catch (SQLException e) {
                listView.showMessage("Error retrieving orders: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }
    
    public void loadFormData() {
        try {
            // Load customers, staff, and items to populate form combo boxes
            if (view != null) {
                view.updateCustomers(dbManager.getAllCustomers());
                view.updateStaff(dbManager.getAllStaff());
                view.updateItems(dbManager.getAllItems());
            }
        } catch (SQLException e) {
            if (view != null) {
                view.showMessage("Error loading form data: " + e.getMessage(), org.eclipse.swt.SWT.ICON_ERROR);
            }
        }
    }
    
    public void searchOrders(String orderId, String customerName, String fromDate, String toDate) {
        try {
            java.util.List<Order> orders;
            
            if (!orderId.isEmpty()) {
                // Search by order ID
                int id = Integer.parseInt(orderId);
                Order order = dbManager.getOrderById(id);
                orders = new ArrayList<>();
                if (order != null) {
                    orders.add(order);
                }
            } else if (!customerName.isEmpty() || !fromDate.isEmpty() || !toDate.isEmpty()) {
                // Search by criteria
                orders = dbManager.searchOrders(customerName, fromDate, toDate);
            } else {
                // No criteria, get all orders
                orders = dbManager.getAllOrders();
            }
            
            // Update the view with search results
            listView.updateCustomerData(dbManager.getAllCustomers());
            listView.displayOrders(orders);
            
        } catch (NumberFormatException e) {
            listView.showMessage("Please enter a valid Order ID", SWT.ICON_ERROR);
        } catch (SQLException e) {
            listView.showMessage("Error searching orders: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }     

    public void assignTableToOrder(int orderId) {
        try {
            // Check if the order is already assigned to a table
            Table existingTable = dbManager.getTableForOrder(orderId);
            if (existingTable != null) {
                listView.showMessage(
                    "Order is already assigned to Table " + existingTable.getNumber(), SWT.ICON_INFORMATION);
                return;
            }
            
            // Get available tables
            List<Table> availableTables = dbManager.getAvailableTables();
            if (availableTables.isEmpty()) {
                listView.showMessage("No tables are currently available", SWT.ICON_INFORMATION);
                return;
            }
            
            // Open table assignment dialog
            TableAssignmentDialog dialog = new TableAssignmentDialog(
                listView.getComposite().getShell(), availableTables);
            Table selectedTable = dialog.open();
            
            if (selectedTable != null) {
                // Assign table to order
                dbManager.assignTableToOrder(selectedTable.getId(), orderId);
                listView.showMessage(
                    "Order assigned to Table " + selectedTable.getNumber(), SWT.ICON_INFORMATION);
                refreshOrderList();
            }
        } catch (SQLException e) {
            listView.showMessage("Error assigning table: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }

    public void releaseTableFromOrder(int orderId) {
        try {
            Table table = dbManager.getTableForOrder(orderId);
            if (table != null) {
                dbManager.releaseTable(table.getId());
                listView.showMessage("Table " + table.getNumber() + " released", SWT.ICON_INFORMATION);
                refreshOrderList();
            } else {
                listView.showMessage("No table is assigned to this order", SWT.ICON_INFORMATION);
            }
        } catch (SQLException e) {
            listView.showMessage("Error releasing table: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }  
    

    public DatabaseManager getDbManager() {
        return dbManager;
    } 
   

}
