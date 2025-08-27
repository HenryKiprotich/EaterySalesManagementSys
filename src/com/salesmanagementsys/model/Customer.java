package com.salesmanagementsys.model;

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String city;
    private String mobileNumber;
    private double debt; 

    public Customer(int id, String firstName, String lastName, String city, String mobileNumber, double debt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.mobileNumber = mobileNumber;     
        this.debt = debt;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getCity() { return city; }
    public String getMobileNumber() { return mobileNumber; }    
    public double getDebt() { return debt; }
}
