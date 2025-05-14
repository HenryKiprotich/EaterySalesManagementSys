package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import com.salesmanagementsys.controller.CustomerController;
import com.salesmanagementsys.model.Customer;
import java.util.List;

public class CustomerListView {
    private Composite composite;
    private Table customerTable;
    private CustomerController controller;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;

    public CustomerListView(Composite parent, CustomerController controller) {
        this.controller = controller;
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = MARGIN;
        layout.marginHeight = MARGIN;
        layout.verticalSpacing = SPACING;
        layout.horizontalSpacing = SPACING;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        createCustomerListSection(parent);
    }

    private void createCustomerListSection(Composite parent) {
        Group listGroup = new Group(composite, SWT.NONE);
        listGroup.setText("Customer List");
        GridLayout listLayout = new GridLayout(1, false);
        listGroup.setLayout(listLayout);
        GridData listData = new GridData(SWT.FILL, SWT.FILL, true, true);
        listGroup.setLayoutData(listData);
        listGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        // Create table for customers with proper layout data
        customerTable = new Table(listGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
        customerTable.setLayoutData(tableData);
        customerTable.setHeaderVisible(true);
        customerTable.setLinesVisible(true);       
   

        String[] titles = {"ID", "First Name", "Last Name", "City", "Mobile Number", "Debt"};
        for (String title : titles) {
            TableColumn column = new TableColumn(customerTable, SWT.NONE);
            column.setText(title);
            column.setWidth(120);
        }

        Composite buttonComp = new Composite(listGroup, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        // Changed to 1 column as we only have the refresh button now
        buttonComp.setLayout(new GridLayout(1, false));
        buttonComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        // Only Refresh button
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
                controller.refreshCustomerList();
            }
        });
    }

    public void displayCustomers(List<Customer> customers) {
        customerTable.removeAll();

     // In the displayCustomers method, update the table item creation:
        for (Customer customer : customers) {
            TableItem item = new TableItem(customerTable, SWT.NONE);
            item.setText(0, String.valueOf(customer.getId()));
            item.setText(1, customer.getFirstName());  // Just firstName
            item.setText(2, customer.getLastName());   // Just lastName
            item.setText(3, customer.getCity());       // City in correct column
            item.setText(4, customer.getMobileNumber()); // Mobile number in correct column
            item.setText(5, String.format("Ksh %.2f", customer.getDebt())); // Debt in correct column
        }

        // Adjust column widths
        for (TableColumn column : customerTable.getColumns()) {
            column.pack();
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
