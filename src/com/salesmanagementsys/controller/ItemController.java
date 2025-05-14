package com.salesmanagementsys.controller;

import com.salesmanagementsys.model.DatabaseManager;
import com.salesmanagementsys.model.Item;
import com.salesmanagementsys.view.ItemListView;
import com.salesmanagementsys.view.ItemView;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.SWT;

public class ItemController {
    private DatabaseManager dbManager;
    private ItemView itemView;
    private ItemListView listView;

    public ItemController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setItemView(ItemView itemView) {
        this.itemView = itemView;
    }

    public void setListView(ItemListView listView) {
        this.listView = listView;
    }

    public void addItem(String name, double price) {
        if (name.trim().isEmpty()) {
            itemView.showMessage("Item name cannot be empty.", SWT.ICON_ERROR);
            return;
        }

        if (price <= 0) {
            itemView.showMessage("Price must be greater than zero.", SWT.ICON_ERROR);
            return;
        }

        try {
            Item item = new Item(0, name, price); // ID will be assigned by database
            dbManager.addItem(item);
            itemView.showMessage("Item added successfully!", SWT.ICON_INFORMATION);
            itemView.clearFields();
            
            if (listView != null) {
                refreshItemList();
            }
        } catch (SQLException e) {
            itemView.showMessage("Error adding item: " + e.getMessage(), SWT.ICON_ERROR);
            e.printStackTrace();
        }
    }

    public void refreshItemList() {
        try {
            List<Item> items = dbManager.getAllItems();
            listView.displayItems(items);
        } catch (SQLException e) {
            listView.showMessage("Error loading items: " + e.getMessage(), SWT.ICON_ERROR);
            e.printStackTrace();
        }
    }
}

