package com.salesmanagementsys.model;

public class Staff {
    private int id;
    private String firstName;
    private String lastName;
    private String hireDate;
    private String jobRole;
    private String username;
    private String password;

    // Add default constructor
    public Staff() {
        // Default constructor
    }

    public Staff(int id, String firstName, String lastName, String hireDate, String jobRole, String username, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hireDate = hireDate;
        this.jobRole = jobRole;
        this.username = username;
        this.password = password;
    }

    // Getters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getHireDate() { return hireDate; }
    public String getJobRole() { return jobRole; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    
    // Add setters
    public void setId(int id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
