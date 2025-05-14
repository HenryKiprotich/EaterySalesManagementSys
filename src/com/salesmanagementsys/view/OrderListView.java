package com.salesmanagementsys.view;

import org.eclipse.swt.*;


import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import com.salesmanagementsys.controller.OrderController;
import com.salesmanagementsys.model.Order;
import com.salesmanagementsys.model.Customer;
import com.salesmanagementsys.model.Staff;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import java.util.Calendar;
import com.salesmanagementsys.model.DatabaseManager;

public class OrderListView {
    private Composite composite;
    private Table orderTable;
    private OrderController controller;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;    
    private Table itemDetailsTable;
    private Group itemDetailsGroup;
    private Button markAsPaidButton;
    private Button editButton;
    private Button deleteButton;
    
    
    // Fields to store customer and staff lists
    private List<Customer> customers = new ArrayList<>();
    private List<Staff> staff;
    
    public void updateCustomerData(List<Customer> customers) {
        this.customers = customers;
    }
    
    private String getCustomerName(int customerId) {
        for (Customer customer : customers) {
            if (customer.getId() == customerId) {
                return customer.getFirstName() + " " + customer.getLastName();
            }
        }
        return "Unknown";
    }


    public OrderListView(Composite parent, OrderController controller) {
        this.controller = controller;
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = MARGIN;
        layout.marginHeight = MARGIN;
        layout.verticalSpacing = SPACING;
        layout.horizontalSpacing = SPACING;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        createOrderListSection(parent);
    }

    private void createOrderListSection(Composite parent) {

    	
	    // Add search panel at the top
	    Group searchGroup = new Group(composite, SWT.NONE);
	    searchGroup.setText("Search Orders");
	    GridLayout searchLayout = new GridLayout(7, false);
	    searchGroup.setLayout(searchLayout);
	    searchGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	    searchGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    
	    // Order ID search
	    Label idLabel = new Label(searchGroup, SWT.NONE);
	    idLabel.setText("Order ID:");
	    idLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    
	    Text idText = new Text(searchGroup, SWT.BORDER);
	    idText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
	    // Customer name search
	    Label customerLabel = new Label(searchGroup, SWT.NONE);
	    customerLabel.setText("Customer:");
	    customerLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    
	    Text customerText = new Text(searchGroup, SWT.BORDER);
	    customerText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
	    // Date range
	    Label dateRangeLabel = new Label(searchGroup, SWT.NONE);
	    dateRangeLabel.setText("Date Range:");
	    dateRangeLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    
	    DateTime fromDate = new DateTime(searchGroup, SWT.DATE | SWT.BORDER);
	    DateTime toDate = new DateTime(searchGroup, SWT.DATE | SWT.BORDER);
	    
	    // Search button row
	    Composite buttonRow = new Composite(searchGroup, SWT.NONE);
	    buttonRow.setLayout(new GridLayout(2, true));
	    buttonRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 7, 1));
	    buttonRow.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    
	    Button searchButton = new Button(buttonRow, SWT.PUSH);
	    searchButton.setText("Search");
	    GridData searchBtnData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
	    searchBtnData.widthHint = 100;
	    searchButton.setLayoutData(searchBtnData);
	    searchButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
	    searchButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	    
	    Button clearButton = new Button(buttonRow, SWT.PUSH);
	    clearButton.setText("Clear");
	    GridData clearBtnData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
	    clearBtnData.widthHint = 100;
	    clearButton.setLayoutData(clearBtnData);
	    
	    // Add the search button listener
	    searchButton.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	            // Get search criteria
	            String orderId = idText.getText().trim();
	            String customerName = customerText.getText().trim();
	            
	            // Format dates as YYYY-MM-DD
	            String fromDateStr = String.format("%04d-%02d-%02d", 
	                fromDate.getYear(), fromDate.getMonth() + 1, fromDate.getDay());
	            String toDateStr = String.format("%04d-%02d-%02d", 
	                toDate.getYear(), toDate.getMonth() + 1, toDate.getDay());
	            
	            // Call search method
	            controller.searchOrders(orderId, customerName, fromDateStr, toDateStr);
	        }
	    });
	    
	    // Add clear button listener
	    clearButton.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	            idText.setText("");
	            customerText.setText("");
	            // Reset dates to current date
	            Calendar now = Calendar.getInstance();
	            fromDate.setDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), 1);
	            toDate.setDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
	            
	            // Refresh original list
	            controller.refreshOrderList();
	        }
	    });        

    	// Code for listGroup and orderTable...
        Group listGroup = new Group(composite, SWT.NONE);
        listGroup.setText("Order List");
        GridLayout listLayout = new GridLayout(1, false);
        listGroup.setLayout(listLayout);
        GridData listData = new GridData(SWT.FILL, SWT.FILL, true, true);
        listData.heightHint = 400;
        listGroup.setLayoutData(listData);
        listGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        //item details section
        itemDetailsGroup = new Group(composite, SWT.NONE);
        itemDetailsGroup.setText("Order Item Details");
        GridLayout detailsLayout = new GridLayout(1, false);
        itemDetailsGroup.setLayout(detailsLayout);
        GridData detailsData = new GridData(SWT.FILL, SWT.FILL, true, true);
        detailsData.heightHint = 150;
        itemDetailsGroup.setLayoutData(detailsData);
        itemDetailsGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        // Create table for orders
        orderTable = new Table(listGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        orderTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        orderTable.setHeaderVisible(true);
        orderTable.setLinesVisible(true);

        // Updated column titles to reflect names instead of IDs
        String[] titles = {"ID", "Customer Name", "Staff Name", "Order Date", "Payment Method", "Total Amount", "Status"};
        for (String title : titles) {
            TableColumn column = new TableColumn(orderTable, SWT.NONE);
            column.setText(title);
            column.setWidth(100);
        }
        
        // Create table for order item details
        itemDetailsTable = new Table(itemDetailsGroup, SWT.BORDER | SWT.FULL_SELECTION);
        itemDetailsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        itemDetailsTable.setHeaderVisible(true);
        itemDetailsTable.setLinesVisible(true);
        
        String[] detailTitles = {"Item Name", "Price", "Quantity", "Subtotal"};
        for (String title : detailTitles) {
            TableColumn column = new TableColumn(itemDetailsTable, SWT.NONE);
            column.setText(title);
            column.setWidth(100);
        }
        
        // Add selection listener to main order table
        orderTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean hasSelection = orderTable.getSelectionCount() > 0;
                markAsPaidButton.setEnabled(hasSelection);
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
                
                if (hasSelection) {
                    TableItem selectedItem = orderTable.getSelection()[0];
                    int orderId = Integer.parseInt(selectedItem.getText(0));
                    controller.showOrderDetails(orderId);
                } else {
                    itemDetailsTable.removeAll();
                }
            }
        });

        // Create buttons composite with more buttons
        Composite buttonComp = new Composite(listGroup, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        buttonComp.setLayout(new GridLayout(4, false)); // Four columns for buttons
        buttonComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
       
        // Mark as Paid button
        markAsPaidButton = new Button(buttonComp, SWT.PUSH);
        markAsPaidButton.setText("Mark as Paid");
        GridData markAsPaidData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        markAsPaidData.widthHint = 100;
        markAsPaidButton.setLayoutData(markAsPaidData);
        markAsPaidButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
        markAsPaidButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        markAsPaidButton.setEnabled(false);
        markAsPaidButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selectedItems = orderTable.getSelection();
                if (selectedItems.length > 0) {
                    // Show confirmation dialog
                    MessageBox confirmDialog = new MessageBox(composite.getShell(),
                        SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                    confirmDialog.setMessage("Are you sure you have received the payment?");
                    confirmDialog.setText("Payment Confirmation");

                    int response = confirmDialog.open();
                    if (response == SWT.YES) {
                        for (TableItem item : selectedItems) {
                            int orderId = Integer.parseInt(item.getText(0));
                            controller.updateOrderPaymentStatus(orderId, "Paid");
                        }
                        controller.refreshOrderList();
                    }
                }
            }
        });
        
        // New Edit button
        editButton = new Button(buttonComp, SWT.PUSH);
        editButton.setText("Edit Order");
        GridData editData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        editData.widthHint = 100;
        editButton.setLayoutData(editData);
        editButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
        editButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        editButton.setEnabled(false);
        editButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selectedItems = orderTable.getSelection();
                if (selectedItems.length > 0) {
                    int orderId = Integer.parseInt(selectedItems[0].getText(0));
                    // Open edit dialog or navigate to edit page
                    showEditOrderDialog(orderId);
                }
            }
        });
        
        // New Delete button
        deleteButton = new Button(buttonComp, SWT.PUSH);
        deleteButton.setText("Delete Order");
        GridData deleteData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        deleteData.widthHint = 100;
        deleteButton.setLayoutData(deleteData);
        deleteButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
        deleteButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        deleteButton.setEnabled(false);
        deleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem[] selectedItems = orderTable.getSelection();
                if (selectedItems.length > 0) {
                    MessageBox confirmBox = new MessageBox(composite.getShell(), 
                                                          SWT.ICON_WARNING | SWT.YES | SWT.NO);
                    confirmBox.setText("Confirm Delete");
                    confirmBox.setMessage("Are you sure you want to delete this order?");
                    
                    if (confirmBox.open() == SWT.YES) {
                        int orderId = Integer.parseInt(selectedItems[0].getText(0));
                        controller.deleteOrder(orderId);
                    }
                }
            }
        });
        
        Button refreshButton = new Button(buttonComp, SWT.PUSH);
        refreshButton.setText("Refresh");
        GridData refreshData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        refreshData.widthHint = 100;
        refreshButton.setLayoutData(refreshData);
        refreshButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
        refreshButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        refreshButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                controller.refreshOrderList();
            }
        });
        
        // New Assign Table button
     Button assignTableButton = new Button(buttonComp, SWT.PUSH);
     assignTableButton.setText("Assign Table");
     GridData assignTableData = new GridData(SWT.FILL, SWT.CENTER, false, false);
     assignTableData.widthHint = 100;
     assignTableButton.setLayoutData(assignTableData);
     assignTableButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
     assignTableButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
     assignTableButton.setEnabled(false);

     Button releaseTableButton = new Button(buttonComp, SWT.PUSH);
     releaseTableButton.setText("Release Table");
     GridData releaseTableData = new GridData(SWT.FILL, SWT.CENTER, false, false);
     releaseTableData.widthHint = 100;
     releaseTableButton.setLayoutData(releaseTableData);
     releaseTableButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_YELLOW));
     releaseTableButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
     releaseTableButton.setEnabled(false);

     // Update selection listener to enable these buttons
     orderTable.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
             boolean hasSelection = orderTable.getSelectionCount() > 0;
             markAsPaidButton.setEnabled(hasSelection);
             editButton.setEnabled(hasSelection);
             deleteButton.setEnabled(hasSelection);
             assignTableButton.setEnabled(hasSelection);
             releaseTableButton.setEnabled(hasSelection);
             
             if (hasSelection) {
                 TableItem selectedItem = orderTable.getSelection()[0];
                 int orderId = Integer.parseInt(selectedItem.getText(0));
                 controller.showOrderDetails(orderId);
             } else {
                 itemDetailsTable.removeAll();
             }
         }
     });

     // Add listeners for the new buttons
     assignTableButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
             TableItem[] selectedItems = orderTable.getSelection();
             if (selectedItems.length > 0) {
                 int orderId = Integer.parseInt(selectedItems[0].getText(0));
                 controller.assignTableToOrder(orderId);
             }
         }
     });

     releaseTableButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
             TableItem[] selectedItems = orderTable.getSelection();
             if (selectedItems.length > 0) {
                 int orderId = Integer.parseInt(selectedItems[0].getText(0));
                 controller.releaseTableFromOrder(orderId);
             }
         }
     });

    }
    
    // Method to display order details
    public void displayOrderDetails(List<DatabaseManager.OrderItemDetail> items) {
        itemDetailsTable.removeAll();
        
        for (DatabaseManager.OrderItemDetail item : items) {
            TableItem tableItem = new TableItem(itemDetailsTable, SWT.NONE);
            tableItem.setText(0, item.getItemName());
            tableItem.setText(1, String.format("$%.2f", item.getPrice()));
            tableItem.setText(2, String.valueOf(item.getQuantity()));
            tableItem.setText(3, String.format("$%.2f", item.getTotalPrice()));
        }
        
        // Adjust column widths
        for (TableColumn column : itemDetailsTable.getColumns()) {
            column.pack();
        }
    }
    
    public void displayOrders(List<Order> orders) {
        orderTable.removeAll();

        try {
            // Get fresh staff list for name lookups if needed
            if (staff == null) staff = controller.getDbManager().getAllStaff();

            for (Order order : orders) {
                TableItem item = new TableItem(orderTable, SWT.NONE);
                item.setText(0, String.valueOf(order.getId()));
                
                // Use the helper method instead of inline lookup
                item.setText(1, getCustomerName(order.getCustomerId()));

                // Find and display staff name instead of ID
                String staffName = "Unknown";
                for (Staff s : staff) {
                    if (s.getId() == order.getStaffId()) {
                        staffName = s.getFirstName() + " " + s.getLastName();
                        break;
                    }
                }
                item.setText(2, staffName);

                item.setText(3, order.getOrderDate());
                item.setText(4, order.getPaymentMethod());
                item.setText(5, String.format("%.2f", order.getTotalAmount()));
                item.setText(6, order.getPaymentStatus());
            }

            // Adjust column widths
            for (TableColumn column : orderTable.getColumns()) {
                column.pack();
            }
        } catch (SQLException e) {
            showMessage("Error retrieving staff data: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }
    
    // Add dialog for editing an order
    private void showEditOrderDialog(int orderId) {
        try {
            Order order = controller.getDbManager().getOrderById(orderId);
            List<DatabaseManager.OrderItemDetail> items = controller.getDbManager().getOrderItemsByOrderId(orderId);
            
            if (order != null) {
                Shell dialog = new Shell(composite.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
                dialog.setText("Edit Order");
                dialog.setLayout(new GridLayout(2, false));
                
                // TODO: Implement edit form with customer, staff selections and item edits
                // This would be a complex UI component similar to OrderView
                // For brevity, show a message that this feature is coming soon
                
                Label message = new Label(dialog, SWT.CENTER);
                message.setText("Edit functionality will be implemented in the next version.");
                message.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 2, 1));
                
                Button closeButton = new Button(dialog, SWT.PUSH);
                closeButton.setText("Close");
                closeButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
                closeButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        dialog.close();
                    }
                });
                
                dialog.pack();
                dialog.open();
            }
        } catch (SQLException e) {
            showMessage("Error retrieving order details: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }    

    public Composite getComposite() {
        return composite;
    }

    public void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(composite.getShell(), style);
        messageBox.setMessage(message);
        messageBox.open();
    }
}
