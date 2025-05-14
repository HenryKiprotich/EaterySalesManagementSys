package com.salesmanagementsys.view;

import org.eclipse.swt.*;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import com.salesmanagementsys.controller.ItemController;
import com.salesmanagementsys.model.Item;
import java.util.List;

public class ItemListView {
    private Composite composite;
    private Table itemTable;
    private ItemController controller;
    private static final int MARGIN = 10;
    private static final int SPACING = 5;

    public ItemListView(Composite parent, ItemController controller) {
        this.controller = controller;
        composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = MARGIN;
        layout.marginHeight = MARGIN;
        layout.verticalSpacing = SPACING;
        layout.horizontalSpacing = SPACING;
        composite.setLayout(layout);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        createItemListSection(parent);
        
        // Register this view with the controller and request initial data load
        controller.setListView(this);
        controller.refreshItemList();
    }

    private void createItemListSection(Composite parent) {
        Group listGroup = new Group(composite, SWT.NONE);
        listGroup.setText("Item List");
        GridLayout listLayout = new GridLayout(1, false);
        listGroup.setLayout(listLayout);
        GridData listData = new GridData(SWT.FILL, SWT.FILL, true, true);
        listData.heightHint = 400;
        listGroup.setLayoutData(listData);
        listGroup.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        // Create table for items
        itemTable = new Table(listGroup, SWT.BORDER | SWT.FULL_SELECTION);
        itemTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        itemTable.setHeaderVisible(true);
        itemTable.setLinesVisible(true);

        String[] titles = {"ID", "Item Name", "Price"};
        for (String title : titles) {
            TableColumn column = new TableColumn(itemTable, SWT.NONE);
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
                controller.refreshItemList();
            }
        });
    }

    public void displayItems(List<Item> itemList) {
        itemTable.removeAll();

        for (Item item : itemList) {
            TableItem tableItem = new TableItem(itemTable, SWT.NONE);
            tableItem.setText(0, String.valueOf(item.getId()));
            tableItem.setText(1, item.getName());
            tableItem.setText(2, String.format("Ksh %.2f", item.getPrice()));
        }

        // Adjust column widths
        for (TableColumn column : itemTable.getColumns()) {
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

