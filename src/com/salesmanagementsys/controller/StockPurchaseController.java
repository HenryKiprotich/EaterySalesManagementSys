package com.salesmanagementsys.controller;

import com.salesmanagementsys.model.DatabaseManager;
import com.salesmanagementsys.model.StockPurchase;
import com.salesmanagementsys.view.StockPurchaseView;
import com.salesmanagementsys.view.StockPurchaseListView;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.SWT;

public class StockPurchaseController {
    private DatabaseManager dbManager;
    private StockPurchaseView purchaseView;
    private StockPurchaseListView listView;

    public StockPurchaseController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setPurchaseView(StockPurchaseView purchaseView) {
        this.purchaseView = purchaseView;
    }

    public void setListView(StockPurchaseListView listView) {
        this.listView = listView;
    }

    public void addStockPurchase(String itemName, double price, int quantity, String date, String notes) {
        if (itemName.trim().isEmpty()) {
            purchaseView.showMessage("Item name cannot be empty.", SWT.ICON_ERROR);
            return;
        }

        if (price <= 0) {
            purchaseView.showMessage("Price must be greater than zero.", SWT.ICON_ERROR);
            return;
        }

        if (quantity <= 0) {
            purchaseView.showMessage("Quantity must be greater than zero.", SWT.ICON_ERROR);
            return;
        }

        if (date.trim().isEmpty()) {
            purchaseView.showMessage("Date cannot be empty.", SWT.ICON_ERROR);
            return;
        }

        try {
            StockPurchase purchase = new StockPurchase(0, itemName, price, quantity, date, notes);
            dbManager.addStockPurchase(purchase);
            purchaseView.showMessage("Stock purchase recorded successfully!", SWT.ICON_INFORMATION);
            purchaseView.clearFields();

            if (listView != null) {
                refreshStockPurchaseList();
            }
        } catch (SQLException e) {
            purchaseView.showMessage("Error recording stock purchase: " + e.getMessage(), SWT.ICON_ERROR);
            e.printStackTrace();
        }
    }

    public void refreshStockPurchaseList() {
        try {
            List<StockPurchase> purchases = dbManager.getAllStockPurchases();
            listView.displayStockPurchases(purchases);
        } catch (SQLException e) {
            listView.showMessage("Error loading stock purchases: " + e.getMessage(), SWT.ICON_ERROR);
            e.printStackTrace();
        }
    }
}

