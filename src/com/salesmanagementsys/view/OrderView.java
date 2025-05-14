package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import com.salesmanagementsys.controller.OrderController;
import com.salesmanagementsys.model.*;
import java.util.List;

public class OrderView {
    private Composite composite;
    private Combo customerCombo, staffCombo, itemCombo, paymentCombo;
    private Text quantityText;
    private org.eclipse.swt.widgets.List orderItemsList;
    private OrderController controller;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;
    private static final int FIELD_WIDTH = 200;

    public OrderView(Composite parent, OrderController controller) {
        this.controller = controller;
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = MARGIN;
        layout.marginHeight = MARGIN;
        layout.verticalSpacing = SPACING;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        // Order Information Section
        Group infoGroup = new Group(composite, SWT.NONE);
        infoGroup.setText("Order Information");
        infoGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout infoLayout = new GridLayout(2, false);
        infoLayout.horizontalSpacing = SPACING;
        infoLayout.verticalSpacing = SPACING;
        infoGroup.setLayout(infoLayout);
        infoGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        new Label(infoGroup, SWT.RIGHT).setText("Customer:");
        customerCombo = new Combo(infoGroup, SWT.READ_ONLY);
        GridData customerData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        customerData.widthHint = FIELD_WIDTH;
        customerCombo.setLayoutData(customerData);
        customerCombo.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        new Label(infoGroup, SWT.RIGHT).setText("Staff:");
        staffCombo = new Combo(infoGroup, SWT.READ_ONLY);
        GridData staffData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        staffData.widthHint = FIELD_WIDTH;
        staffCombo.setLayoutData(staffData);
        staffCombo.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        // Item Selection Section
        Group itemGroup = new Group(composite, SWT.NONE);
        itemGroup.setText("Add Items");
        itemGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout itemLayout = new GridLayout(2, false); // Changed back to 2 columns since button is moved
        itemLayout.horizontalSpacing = SPACING;
        itemLayout.verticalSpacing = SPACING;
        itemGroup.setLayout(itemLayout);
        itemGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        new Label(itemGroup, SWT.RIGHT).setText("Item:");
        itemCombo = new Combo(itemGroup, SWT.READ_ONLY);
        GridData itemData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        itemData.widthHint = FIELD_WIDTH;
        itemCombo.setLayoutData(itemData);
        itemCombo.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        new Label(itemGroup, SWT.RIGHT).setText("Quantity:");
        quantityText = new Text(itemGroup, SWT.BORDER);
        GridData quantityData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        quantityData.widthHint = 50;
        quantityText.setLayoutData(quantityData);
        quantityText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        // Add Item button in a separate composite aligned to the right
        Composite buttonCompAddItem = new Composite(composite, SWT.NONE);
        buttonCompAddItem.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        buttonCompAddItem.setLayout(new GridLayout(1, false));
        buttonCompAddItem.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        Button addItemButton = new Button(buttonCompAddItem, SWT.PUSH);
        addItemButton.setText("Add Item");
        GridData addItemButtonData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        addItemButtonData.widthHint = 120;
        addItemButton.setLayoutData(addItemButtonData);
        addItemButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
        addItemButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        addItemButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                controller.addItemToOrder(itemCombo.getText(), quantityText.getText(), orderItemsList);
            }
        });

        // Order Items List
        Label itemsLabel = new Label(composite, SWT.NONE);
        itemsLabel.setText("Order Items:");
        itemsLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
        
        orderItemsList = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.V_SCROLL);
        GridData listGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        listGridData.heightHint = 120;
        orderItemsList.setLayoutData(listGridData);
        orderItemsList.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        // Checkout Section
        Group checkoutGroup = new Group(composite, SWT.NONE);
        checkoutGroup.setText("Checkout");
        checkoutGroup.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        GridLayout checkoutLayout = new GridLayout(2, false);
        checkoutLayout.horizontalSpacing = SPACING;
        checkoutLayout.verticalSpacing = SPACING;
        checkoutGroup.setLayout(checkoutLayout);
        checkoutGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        new Label(checkoutGroup, SWT.RIGHT).setText("Payment Method:");
        paymentCombo = new Combo(checkoutGroup, SWT.READ_ONLY);
        paymentCombo.setItems("cash", "card");
        GridData paymentData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        paymentData.widthHint = FIELD_WIDTH;
        paymentCombo.setLayoutData(paymentData);
        paymentCombo.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        // Button Section
        Composite buttonComp = new Composite(composite, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        buttonComp.setLayout(new GridLayout(1, false));
        buttonComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        Button placeOrderButton = new Button(buttonComp, SWT.PUSH);
        placeOrderButton.setText("Place Order");
        GridData buttonData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        buttonData.widthHint = 120;
        placeOrderButton.setLayoutData(buttonData);
        placeOrderButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
        placeOrderButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        placeOrderButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                controller.placeOrder(
                    customerCombo.getText(),
                    staffCombo.getText(),
                    paymentCombo.getText(),
                    orderItemsList.getItems()
                );
            }
        });
    }

    public Composite getComposite() {
        return composite;
    }

    public void updateCustomers(List<Customer> customers) {
        customerCombo.removeAll();
        customerCombo.add("None");
        for (Customer c : customers) {
            customerCombo.add(c.getId() + " - " + c.getFirstName() + " " + c.getLastName());
        }
    }

    public void updateStaff(List<Staff> staff) {
        staffCombo.removeAll();
        for (Staff s : staff) {
            staffCombo.add(s.getId() + " - " + s.getFirstName() + " " + s.getLastName());
        }
    }

    public void updateItems(List<com.salesmanagementsys.model.Item> items) {
        itemCombo.removeAll();
        for (com.salesmanagementsys.model.Item i : items) {
            itemCombo.add(i.getName() + " - $" + i.getPrice());
        }
    }

    public void clearOrder() {
        orderItemsList.removeAll();
        quantityText.setText("");
        customerCombo.select(0);
        staffCombo.deselectAll();
        paymentCombo.deselectAll();
    }

    public void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(composite.getShell(), style);
        messageBox.setMessage(message);
        messageBox.open();
    }
}