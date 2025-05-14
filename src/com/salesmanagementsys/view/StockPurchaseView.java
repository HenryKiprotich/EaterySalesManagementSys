package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import com.salesmanagementsys.controller.StockPurchaseController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StockPurchaseView {
    private Composite composite;
    private StockPurchaseController controller;
    private Text itemNameText;
    private Text priceText;
    private Text quantityText;
    private Text dateText;
    private Text notesText;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;

    public StockPurchaseView(Composite parent, StockPurchaseController controller) {
        this.controller = controller;
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = MARGIN;
        layout.marginHeight = MARGIN;
        layout.verticalSpacing = SPACING;
        layout.horizontalSpacing = SPACING;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        createAddStockPurchaseSection(parent);

        // Register this view with the controller
        controller.setPurchaseView(this);
    }

    private void createAddStockPurchaseSection(Composite parent) {
        Group addPurchaseGroup = new Group(composite, SWT.NONE);
        addPurchaseGroup.setText("Record Stock Purchase");
        GridLayout formLayout = new GridLayout(2, false);
        formLayout.marginWidth = MARGIN;
        formLayout.marginHeight = MARGIN;
        formLayout.verticalSpacing = SPACING;
        addPurchaseGroup.setLayout(formLayout);
        GridData formData = new GridData(SWT.FILL, SWT.FILL, true, false);
        addPurchaseGroup.setLayoutData(formData);
        addPurchaseGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        // Item Name
        Label nameLabel = new Label(addPurchaseGroup, SWT.NONE);
        nameLabel.setText("Item Name:");
        nameLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        itemNameText = new Text(addPurchaseGroup, SWT.BORDER);
        itemNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Price
        Label priceLabel = new Label(addPurchaseGroup, SWT.NONE);
        priceLabel.setText("Price:");
        priceLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        priceText = new Text(addPurchaseGroup, SWT.BORDER);
        priceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Quantity
        Label quantityLabel = new Label(addPurchaseGroup, SWT.NONE);
        quantityLabel.setText("Quantity:");
        quantityLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        quantityText = new Text(addPurchaseGroup, SWT.BORDER);
        quantityText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        quantityText.setText("1");

        // Date
        Label dateLabel = new Label(addPurchaseGroup, SWT.NONE);
        dateLabel.setText("Purchase Date (YYYY-MM-DD):");
        dateLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        dateText = new Text(addPurchaseGroup, SWT.BORDER);
        dateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        // Set today's date as default
        dateText.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        // Notes
        Label notesLabel = new Label(addPurchaseGroup, SWT.NONE);
        notesLabel.setText("Notes:");
        notesLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        notesText = new Text(addPurchaseGroup, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData notesData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        notesData.heightHint = 60;
        notesText.setLayoutData(notesData);

        // Add Button
        Composite buttonComp = new Composite(addPurchaseGroup, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
        buttonComp.setLayout(new GridLayout(1, false));
        buttonComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        Button addButton = new Button(buttonComp, SWT.PUSH);
        addButton.setText("Record Purchase");
        GridData addData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        addData.widthHint = 150;
        addButton.setLayoutData(addData);
        addButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN));
        addButton.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    String itemName = itemNameText.getText();
                    double price = Double.parseDouble(priceText.getText());
                    int quantity = Integer.parseInt(quantityText.getText());
                    String date = dateText.getText();
                    String notes = notesText.getText();
                    controller.addStockPurchase(itemName, price, quantity, date, notes);
                } catch (NumberFormatException ex) {
                    showMessage("Please enter valid numbers for price and quantity.", SWT.ICON_ERROR);
                }
            }
        });
    }

    public Composite getComposite() {
        return composite;
    }

    public void clearFields() {
        itemNameText.setText("");
        priceText.setText("");
        quantityText.setText("1");
        notesText.setText("");
        itemNameText.setFocus();
    }

    public void showMessage(String message, int style) {
        MessageBox messageBox = new MessageBox(composite.getShell(), style);
        messageBox.setMessage(message);
        messageBox.open();
    }
}

