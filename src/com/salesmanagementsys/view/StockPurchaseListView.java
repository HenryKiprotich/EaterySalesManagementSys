package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import com.salesmanagementsys.controller.StockPurchaseController;
import com.salesmanagementsys.model.StockPurchase;
import java.util.List;

public class StockPurchaseListView {
    private Composite composite;
    private Table purchaseTable;
    private StockPurchaseController controller;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;

    public StockPurchaseListView(Composite parent, StockPurchaseController controller) {
        this.controller = controller;
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = MARGIN;
        layout.marginHeight = MARGIN;
        layout.verticalSpacing = SPACING;
        layout.horizontalSpacing = SPACING;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        createStockPurchaseListSection(parent);

        // Register this view with the controller and request initial data load
        controller.setListView(this);
        controller.refreshStockPurchaseList();
    }

    private void createStockPurchaseListSection(Composite parent) {
        Group listGroup = new Group(composite, SWT.NONE);
        listGroup.setText("Stock Purchase Records");
        GridLayout listLayout = new GridLayout(1, false);
        listGroup.setLayout(listLayout);
        GridData listData = new GridData(SWT.FILL, SWT.FILL, true, true);
        listData.heightHint = 400;
        listGroup.setLayoutData(listData);
        listGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        // Create table for stock purchases
        purchaseTable = new Table(listGroup, SWT.BORDER | SWT.FULL_SELECTION);
        purchaseTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        purchaseTable.setHeaderVisible(true);
        purchaseTable.setLinesVisible(true);

        String[] titles = {"ID", "Item Name", "Price", "Quantity", "Total Cost", "Purchase Date", "Notes"};
        for (String title : titles) {
            TableColumn column = new TableColumn(purchaseTable, SWT.NONE);
            column.setText(title);
            column.setWidth(100);
        }

        Composite buttonComp = new Composite(listGroup, SWT.NONE);
        buttonComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        buttonComp.setLayout(new GridLayout(1, false));
        buttonComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

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
                controller.refreshStockPurchaseList();
            }
        });
    }

    public void displayStockPurchases(List<StockPurchase> purchaseList) {
        purchaseTable.removeAll();

        for (StockPurchase purchase : purchaseList) {
            TableItem tableItem = new TableItem(purchaseTable, SWT.NONE);
            tableItem.setText(0, String.valueOf(purchase.getId()));
            tableItem.setText(1, purchase.getItemName());
            tableItem.setText(2, String.format("Ksh %.2f", purchase.getPrice()));
            tableItem.setText(3, String.valueOf(purchase.getQuantity()));
            double totalCost = purchase.getPrice() * purchase.getQuantity();
            tableItem.setText(4, String.format("Ksh %.2f", totalCost));
            tableItem.setText(5, purchase.getPurchaseDate());
            tableItem.setText(6, purchase.getNotes() != null ? purchase.getNotes() : "");
        }

        // Adjust column widths
        for (TableColumn column : purchaseTable.getColumns()) {
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

