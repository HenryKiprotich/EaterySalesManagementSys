package com.salesmanagementsys.model;

public class OrderItem {
    private int id;
    private Integer orderId;
    private Integer preorderId;
    private int itemId;
    private int quantity;

    public OrderItem(int id, Integer orderId, Integer preorderId, int itemId, int quantity) {
        this.id = id;
        this.orderId = orderId;
        this.preorderId = preorderId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public Integer getOrderId() { return orderId; }
    public Integer getPreorderId() { return preorderId; }
    public int getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
}
