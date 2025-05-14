package com.salesmanagementsys.model;

public class JobRole {
    private int id;
    private String name;
    private String description;
    
    public JobRole(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    
    @Override
    public String toString() {
        return name;
    }
}

