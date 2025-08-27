package com.salesmanagementsys.model;

public class Order {
    private int id;
    private int customerId;
    private int staffId;
    private String orderDate;
    private String paymentMethod;
    private double totalAmount;
    private String paymentStatus; 

    public Order(int id, int customerId, int staffId, String orderDate, String paymentMethod, double totalAmount, String paymentStatus) {
        this.id = id;
        this.customerId = customerId;
        this.staffId = staffId;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
    }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getStaffId() { return staffId; }
    public String getOrderDate() { return orderDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
}
