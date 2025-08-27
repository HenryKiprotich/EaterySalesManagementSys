package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import com.salesmanagementsys.controller.PreOrderController;
import com.salesmanagementsys.model.*;
import java.util.List;

public class PreOrderView {
    private Composite composite;
    private Combo customerCombo, staffCombo, itemCombo;
    private Text quantityText, collectionDateText, collectionTimeText;
    private org.eclipse.swt.widgets.List orderItemsList;
    private PreOrderController controller;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;
    private static final int FIELD_WIDTH = 200;

    public PreOrderView(Composite parent, PreOrderController controller) {
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
        infoGroup.setText("Pre-Order Information");
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
        
        // Item button in a separate composite aligned to the right
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
                controller.addItemToPreOrder(itemCombo.getText(), quantityText.getText(), orderItemsList);
            }
        });

        // Order Items List
        Label itemsLabel = new Label(composite, SWT.NONE);
        itemsLabel.setText("Pre-Order Items:");
        itemsLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
        
        orderItemsList = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.V_SCROLL);
        GridData listGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        listGridData.heightHint = 120;
        orderItemsList.setLayoutData(listGridData);
        orderItemsList.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        // Collection Details Section
        Group collectionGroup = new Group(composite, SWT.NONE);
        collectionGroup.setText("Collection Details");
        collectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        GridLayout collectionLayout = new GridLayout(2, false);
        collectionLayout.horizontalSpacing = SPACING;
        collectionLayout.verticalSpacing = SPACING;
        collectionGroup.setLayout(collectionLayout);
        collectionGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        new Label(collectionGroup, SWT.RIGHT).setText("Collection Date:");
        collectionDateText = new Text(collectionGroup, SWT.BORDER);
        collectionDateText.setMessage("YYYY-MM-DD");
        GridData dateData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        dateData.widthHint = FIELD_WIDTH;
        collectionDateText.setLayoutData(dateData);
        collectionDateText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        new Label(collectionGroup, SWT.RIGHT).setText("Collection Time:");
        collectionTimeText = new Text(collectionGroup, SWT.BORDER);
        collectionTimeText.setMessage("HH:MM");
        GridData timeData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        timeData.widthHint = FIELD_WIDTH;
        collectionTimeText.setLayoutData(timeData);
        collectionTimeText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        
        // Button Section
        Composite buttonComp = new Composite(composite, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        buttonComp.setLayout(new GridLayout(1, false));
        buttonComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        Button placePreOrderButton = new Button(buttonComp, SWT.PUSH);
        placePreOrderButton.setText("Place Pre-Order");
        GridData buttonData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        buttonData.widthHint = 120;
        placePreOrderButton.setLayoutData(buttonData);
        placePreOrderButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
        placePreOrderButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        placePreOrderButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                controller.placePreOrder(
                    customerCombo.getText(),
                    staffCombo.getText(),
                    collectionDateText.getText(),
                    collectionTimeText.getText(),
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

    public void clearPreOrder() {
        orderItemsList.removeAll();
        quantityText.setText("");
        collectionDateText.setText("");
        collectionTimeText.setText("");
        customerCombo.deselectAll();
        staffCombo.deselectAll();
    }

    public void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(composite.getShell(), style);
        messageBox.setMessage(message);
        messageBox.open();
    }
}