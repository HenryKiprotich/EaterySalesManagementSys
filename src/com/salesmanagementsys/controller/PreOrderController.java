package com.salesmanagementsys.controller;

import com.salesmanagementsys.model.DatabaseManager;



import com.salesmanagementsys.model.Item;
import com.salesmanagementsys.model.PreOrder;
import com.salesmanagementsys.model.OrderItem;
import com.salesmanagementsys.view.PreOrderView;
import com.salesmanagementsys.view.PreOrderListView;
import com.salesmanagementsys.view.TableAssignmentDialog;
import java.util.ArrayList;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.eclipse.swt.SWT;
import com.salesmanagementsys.model.Table;
import java.util.List;

public class PreOrderController {
    private PreOrderView view;
    private PreOrderListView listView;
    private DatabaseManager dbManager;
    
    public PreOrderController(PreOrderView view, DatabaseManager dbManager) {
        this.view = view;
        this.dbManager = dbManager;
    }
    
    public void setView(PreOrderView view) {
        this.view = view;
    }
    
    public void setListView(PreOrderListView listView) {
        this.listView = listView;
    }
    
    // Method to handle adding items to the pre-order
    // This method is called when the user selects an item and enters a quantity
    public void addItemToPreOrder(String itemText, String quantityText, org.eclipse.swt.widgets.List orderItemsList) {
        if (itemText == null || itemText.isEmpty()) {
            view.showMessage("Please select an item", org.eclipse.swt.SWT.ICON_ERROR);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                view.showMessage("Quantity must be greater than zero", org.eclipse.swt.SWT.ICON_ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            view.showMessage("Please enter a valid quantity", org.eclipse.swt.SWT.ICON_ERROR);
            return;
        }

        // Add item to the UI list
        orderItemsList.add(quantity + "x " + itemText);
    }

    
    // Method to process the pre-order placement
    public void placePreOrder(String customerText, String staffText, String collectionDate, 
                             String collectionTime, String[] orderItems) {
        if (customerText == null || customerText.isEmpty() || customerText.equals("None")) {
            view.showMessage("Please select a customer", org.eclipse.swt.SWT.ICON_ERROR);
            return;
        }

        if (staffText == null || staffText.isEmpty()) {
            view.showMessage("Please select a staff member", org.eclipse.swt.SWT.ICON_ERROR);
            return;
        }

        if (collectionDate == null || collectionDate.isEmpty()) {
            view.showMessage("Please enter a collection date", org.eclipse.swt.SWT.ICON_ERROR);
            return;
        }

        if (collectionTime == null || collectionTime.isEmpty()) {
            view.showMessage("Please enter a collection time", org.eclipse.swt.SWT.ICON_ERROR);
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
            
            // Get current date for preorder date
            String preorderDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            // Create and save the pre-order with correct items
            createPreOrder(customerId, staffId, preorderDate, collectionDate, collectionTime, totalAmount, itemsList);
            
            // Clear the pre-order form
            view.clearPreOrder();
            
        } catch (Exception e) {
            view.showMessage("Error processing pre-order: " + e.getMessage(), org.eclipse.swt.SWT.ICON_ERROR);
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
    

 // createPreOrder that accepts itemsList
 public void createPreOrder(int customerId, int staffId, String preorderDate, String collectionDate, 
                           String collectionTime, double totalAmount, java.util.List<OrderItem> orderItems) {
     try {
         // Create PreOrder object
         PreOrder preOrder = new PreOrder(0, customerId, staffId, preorderDate, collectionDate, 
                                         collectionTime, totalAmount, "Pending");
         
         int preorderId = dbManager.addPreOrder(preOrder, orderItems);
         
         // Add the pre-order amount to the customer's debt
         dbManager.updateCustomerDebt(customerId, totalAmount);
         
         if (view != null) {
             view.showMessage("Pre-Order created successfully", org.eclipse.swt.SWT.ICON_INFORMATION);
         }
         refreshPreOrderList();
     } catch (SQLException e) {
         if (view != null) {
             view.showMessage("Error creating pre-order: " + e.getMessage(), org.eclipse.swt.SWT.ICON_ERROR);
         }
     }
 }

    public void updatePreOrderPaymentStatus(int preOrderId, String status) {
        try {
            // Get the pre-order details
            PreOrder preOrder = dbManager.getPreOrderById(preOrderId);
            
            if (preOrder != null && "Paid".equals(status)) {
                // Reduce the debt by the pre-order amount
                dbManager.updateCustomerDebt(preOrder.getCustomerId(), -preOrder.getTotalAmount());
            }
            
            dbManager.updatePreOrderPaymentStatus(preOrderId, status);
            refreshPreOrderList();
        } catch (SQLException e) {
            if (listView != null) {
                listView.showMessage("Error updating payment status: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }    

    public void deletePreOrder(int preOrderId) {
        try {
            // Get the preorder to access customer info for debt adjustment
            PreOrder preOrder = dbManager.getPreOrderById(preOrderId);
            
            if (preOrder != null) {
                if ("Paid".equals(preOrder.getPaymentStatus())) {
                    // If the preorder was paid, adjust the debt back
                    dbManager.updateCustomerDebt(preOrder.getCustomerId(), preOrder.getTotalAmount());
                } else {
                    // If preorder wasn't paid, remove the debt
                    dbManager.updateCustomerDebt(preOrder.getCustomerId(), -preOrder.getTotalAmount());
                }
                
                dbManager.deletePreOrder(preOrderId);
                refreshPreOrderList();
                if (listView != null) {
                    listView.showMessage("Pre-Order deleted successfully", SWT.ICON_INFORMATION);
                }
            }
        } catch (SQLException e) {
            if (listView != null) {
                listView.showMessage("Error deleting pre-order: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }
 
    public void showPreOrderDetails(int preOrderId) {
        try {
            java.util.List<DatabaseManager.OrderItemDetail> items = dbManager.getOrderItemsByPreOrderId(preOrderId);
            if (listView != null) {
                listView.displayPreOrderDetails(items);
            }
        } catch (SQLException e) {
            if (listView != null) {
                listView.showMessage("Error retrieving pre-order details: " + e.getMessage(), SWT.ICON_ERROR);
            }
        }
    }
    
 
    public void refreshPreOrderList() {
        if (listView != null) {
            try {
                java.util.List<PreOrder> preOrders = dbManager.getAllPreOrders();
                // Load the latest customer data before displaying preorders
                listView.updateCustomerData(dbManager.getAllCustomers());
                listView.displayPreOrders(preOrders);
            } catch (SQLException e) {
                listView.showMessage("Error retrieving pre-orders: " + e.getMessage(), org.eclipse.swt.SWT.ICON_ERROR);
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
    
    public void searchPreOrders(String preOrderId, String customerName, String fromDate, String toDate) {
        try {
            java.util.List<PreOrder> preOrders;
            
            if (!preOrderId.isEmpty()) {
                // Search by preOrder ID
                int id = Integer.parseInt(preOrderId);
                PreOrder preOrder = dbManager.getPreOrderById(id);
                preOrders = new ArrayList<>();
                if (preOrder != null) {
                    preOrders.add(preOrder);
                }
            } else if (!customerName.isEmpty() || !fromDate.isEmpty() || !toDate.isEmpty()) {
                // Search by criteria
                preOrders = dbManager.searchPreOrders(customerName, fromDate, toDate);
            } else {
                // No criteria, get all preOrders
                preOrders = dbManager.getAllPreOrders();
            }
            
            // Update the view with search results
            listView.updateCustomerData(dbManager.getAllCustomers());
            listView.displayPreOrders(preOrders);
            
        } catch (NumberFormatException e) {
            listView.showMessage("Please enter a valid Pre-Order ID", SWT.ICON_ERROR);
        } catch (SQLException e) {
            listView.showMessage("Error searching pre-orders: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }    
 
    public void assignTableToPreOrder(int preOrderId) {
        try {
            // Check if the preorder is already assigned to a table
            Table existingTable = dbManager.getTableForPreOrder(preOrderId);
            if (existingTable != null) {
                listView.showMessage(
                    "Pre-Order is already assigned to Table " + existingTable.getNumber(), SWT.ICON_INFORMATION);
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
                // Assign table to preorder
                dbManager.assignTableToPreOrder(selectedTable.getId(), preOrderId);
                listView.showMessage(
                    "Pre-Order assigned to Table " + selectedTable.getNumber(), SWT.ICON_INFORMATION);
                refreshPreOrderList();
            }
        } catch (SQLException e) {
            listView.showMessage("Error assigning table: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }

    public void releaseTableFromPreOrder(int preOrderId) {
        try {
            Table table = dbManager.getTableForPreOrder(preOrderId);
            if (table != null) {
                dbManager.releaseTable(table.getId());
                listView.showMessage("Table " + table.getNumber() + " released", SWT.ICON_INFORMATION);
                refreshPreOrderList();
            } else {
                listView.showMessage("No table is assigned to this pre-order", SWT.ICON_INFORMATION);
            }
        } catch (SQLException e) {
            listView.showMessage("Error releasing table: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }    
    
 
    public DatabaseManager getDbManager() {
        return dbManager;
    }

}
