package com.salesmanagementsys.model;

public class Table {
    private int id;
    private String number;
    private int capacity;
    private String status; // Available, Occupied, Reserved
    private Integer currentOrderId;
    private Integer currentPreOrderId;
    
    public Table(int id, String number, int capacity, String status) {
        this.id = id;
        this.number = number;
        this.capacity = capacity;
        this.status = status;
        this.currentOrderId = null;
        this.currentPreOrderId = null;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getCurrentOrderId() { return currentOrderId; }
    public void setCurrentOrderId(Integer currentOrderId) { this.currentOrderId = currentOrderId; }
    
    public Integer getCurrentPreOrderId() { return currentPreOrderId; }
    public void setCurrentPreOrderId(Integer currentPreOrderId) { this.currentPreOrderId = currentPreOrderId; }
}

