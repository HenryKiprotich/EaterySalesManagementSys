package com.salesmanagementsys.model;

public class PreOrder {
    private int id;
    private int customerId;
    private int staffId;
    private String preorderDate;
    private String collectionDate;
    private String collectionTime;
    private double totalAmount;
    private String paymentStatus; 

    public PreOrder(int id, int customerId, int staffId, String preorderDate, String collectionDate, String collectionTime, double totalAmount, String paymentStatus) {
        this.id = id;
        this.customerId = customerId;
        this.staffId = staffId;
        this.preorderDate = preorderDate;
        this.collectionDate = collectionDate;
        this.collectionTime = collectionTime;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
    }
    //getters
    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getStaffId() { return staffId; }
    public String getPreorderDate() { return preorderDate; }
    public String getCollectionDate() { return collectionDate; }
    public String getCollectionTime() { return collectionTime; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
}
