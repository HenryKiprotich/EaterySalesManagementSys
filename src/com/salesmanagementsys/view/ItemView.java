package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import com.salesmanagementsys.controller.ItemController;

public class ItemView {
    private Composite composite;
    private ItemController controller;
    private Text nameText;
    private Text priceText;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;

    public ItemView(Composite parent, ItemController controller) {
        this.controller = controller;
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = MARGIN;
        layout.marginHeight = MARGIN;
        layout.verticalSpacing = SPACING;
        layout.horizontalSpacing = SPACING;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        createAddItemSection(parent);
        
        // Register this view with the controller
        controller.setItemView(this);
    }

    private void createAddItemSection(Composite parent) {
        Group addItemGroup = new Group(composite, SWT.NONE);
        addItemGroup.setText("Add New Item");
        GridLayout formLayout = new GridLayout(2, false);
        formLayout.marginWidth = MARGIN;
        formLayout.marginHeight = MARGIN;
        formLayout.verticalSpacing = SPACING;
        addItemGroup.setLayout(formLayout);
        GridData formData = new GridData(SWT.FILL, SWT.FILL, true, false);
        addItemGroup.setLayoutData(formData);
        addItemGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        // Item Name
        Label nameLabel = new Label(addItemGroup, SWT.NONE);
        nameLabel.setText("Item Name:");
        nameLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        nameText = new Text(addItemGroup, SWT.BORDER);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Item Price
        Label priceLabel = new Label(addItemGroup, SWT.NONE);
        priceLabel.setText("Price:");
        priceLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        priceText = new Text(addItemGroup, SWT.BORDER);
        priceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Add Button
        Composite buttonComp = new Composite(addItemGroup, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
        buttonComp.setLayout(new GridLayout(1, false));
        buttonComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        Button addButton = new Button(buttonComp, SWT.PUSH);
        addButton.setText("Add Item");
        GridData addData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        addData.widthHint = 100;
        addButton.setLayoutData(addData);
        addButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
        addButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    String name = nameText.getText();
                    double price = Double.parseDouble(priceText.getText());
                    controller.addItem(name, price);
                } catch (NumberFormatException ex) {
                    showMessage("Please enter a valid price.", SWT.ICON_ERROR);
                }
            }
        });
    }

    public Composite getComposite() {
        return composite;
    }

    public void clearFields() {
        nameText.setText("");
        priceText.setText("");
        nameText.setFocus();
    }

    public void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(composite.getShell(), style);
        messageBox.setMessage(message);
        messageBox.open();
    }
}
