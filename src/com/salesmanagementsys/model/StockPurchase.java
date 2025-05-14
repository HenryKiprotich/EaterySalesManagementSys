package com.salesmanagementsys.model;

public class StockPurchase {
    private int id;
    private String itemName;
    private double price;
    private int quantity;
    private String purchaseDate;
    private String notes;

    public StockPurchase(int id, String itemName, double price, int quantity, String purchaseDate, String notes) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.notes = notes;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getNotes() {
        return notes;
    }
}

