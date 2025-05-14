// Create new file: TableManagementView.java in com.amazingdessertbar.view package
package com.salesmanagementsys.view;

import org.eclipse.swt.SWT;

import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import com.salesmanagementsys.model.Table;
import com.salesmanagementsys.model.DatabaseManager;
import java.util.List;
import java.sql.SQLException;

public class TableManagementView {
    private Composite composite;
    private DatabaseManager dbManager;
    private org.eclipse.swt.widgets.Table tableList;
    
    public TableManagementView(Composite parent, DatabaseManager dbManager) {
        this.dbManager = dbManager;
        
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        composite.setLayout(layout);
        
        createTableManagementSection(parent);
    }
    
    private void createTableManagementSection(Composite parent) {
        Group listGroup = new Group(composite, SWT.NONE);
        listGroup.setText("Manage Tables");
        GridLayout listLayout = new GridLayout(1, false);
        listGroup.setLayout(listLayout);
        GridData listData = new GridData(SWT.FILL, SWT.FILL, true, true);
        listData.heightHint = 400;
        listGroup.setLayoutData(listData);
        
        // Create table list
        tableList = new org.eclipse.swt.widgets.Table(listGroup, SWT.BORDER | SWT.FULL_SELECTION);
        tableList.setHeaderVisible(true);
        tableList.setLinesVisible(true);
        tableList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        String[] titles = {"ID", "Table Number", "Capacity", "Status", "Current Order", "Current Pre-Order"};
        for (String title : titles) {
            TableColumn column = new TableColumn(tableList, SWT.NONE);
            column.setText(title);
            column.setWidth(100);
        }
        
        // Create form for adding new tables
        Group addGroup = new Group(composite, SWT.NONE);
        addGroup.setText("Add New Table");
        GridLayout addLayout = new GridLayout(2, false);
        addGroup.setLayout(addLayout);
        addGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        // Table number field
        Label numberLabel = new Label(addGroup, SWT.NONE);
        numberLabel.setText("Table Number:");
        
        Text numberText = new Text(addGroup, SWT.BORDER);
        numberText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        // Capacity field
        Label capacityLabel = new Label(addGroup, SWT.NONE);
        capacityLabel.setText("Capacity:");
        
        Text capacityText = new Text(addGroup, SWT.BORDER);
        capacityText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        // Button composite
        Composite buttonComp = new Composite(addGroup, SWT.NONE);
        buttonComp.setLayout(new GridLayout(3, true));
        buttonComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        
        // Add table button
        Button addButton = new Button(buttonComp, SWT.PUSH);
        addButton.setText("Add Table");
        GridData addData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        addButton.setLayoutData(addData);
        
        // Refresh button
        Button refreshButton = new Button(buttonComp, SWT.PUSH);
        refreshButton.setText("Refresh");
        GridData refreshData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        refreshButton.setLayoutData(refreshData);
        
        // Delete button
        Button deleteButton = new Button(buttonComp, SWT.PUSH);
        deleteButton.setText("Delete Selected");
        GridData deleteData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        deleteButton.setLayoutData(deleteData);
        deleteButton.setEnabled(false);
        
        // Add selection listener for table list
        tableList.addListener(SWT.Selection, event -> {
            deleteButton.setEnabled(tableList.getSelectionCount() > 0);
        });
        
        // Add button listener
        addButton.addListener(SWT.Selection, event -> {
            try {
                String number = numberText.getText().trim();
                int capacity = Integer.parseInt(capacityText.getText().trim());
                
                if (number.isEmpty()) {
                    showMessage("Table number cannot be empty", SWT.ICON_ERROR);
                    return;
                }
                
                if (capacity <= 0) {
                    showMessage("Capacity must be greater than zero", SWT.ICON_ERROR);
                    return;
                }
                
                Table newTable = new Table(0, number, capacity, "Available");
                dbManager.addTable(newTable);
                
                numberText.setText("");
                capacityText.setText("");
                
                refreshTableList();
                showMessage("Table added successfully", SWT.ICON_INFORMATION);
            } catch (NumberFormatException e) {
                showMessage("Please enter a valid capacity", SWT.ICON_ERROR);
            } catch (SQLException e) {
                showMessage("Error adding table: " + e.getMessage(), SWT.ICON_ERROR);
            }
        });
        
        // Refresh button listener
        refreshButton.addListener(SWT.Selection, event -> {
            refreshTableList();
        });
        
        // Initialize table list
        refreshTableList();
    }
    
 // In TableManagementView.java - modify refreshTableList() method
    private void refreshTableList() {
        tableList.removeAll();
        
        try {
            // Get all tables with order information using a join query
            List<Object[]> tablesWithOrders = dbManager.getTablesWithOrderDetails();
            
            for (Object[] tableData : tablesWithOrders) {
                Table table = (Table) tableData[0];
                String orderDetails = (String) tableData[1]; // Contains customer name and order info
                String preOrderDetails = (String) tableData[2]; // Contains customer name and preorder info
                
                TableItem item = new TableItem(tableList, SWT.NONE);
                item.setText(0, String.valueOf(table.getId()));
                item.setText(1, table.getNumber());
                item.setText(2, String.valueOf(table.getCapacity()));
                item.setText(3, table.getStatus());
                item.setText(4, orderDetails != null ? orderDetails : "None");
                item.setText(5, preOrderDetails != null ? preOrderDetails : "None");
            }
            
            // Adjust column widths
            for (TableColumn column : tableList.getColumns()) {
                column.pack();
            }
        } catch (SQLException e) {
            showMessage("Error loading tables: " + e.getMessage(), SWT.ICON_ERROR);
        }
    }

    
    public Composite getComposite() {
        return composite;
    }
    
    private void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(composite.getShell(), style);
        messageBox.setMessage(message);
        messageBox.open();
    }
}

