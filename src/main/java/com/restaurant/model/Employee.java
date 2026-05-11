package com.restaurant.model;

public class Employee {
    private int id;
    private String name;
    private String role;
    private String pin; // Added PIN for authentication

    public Employee(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public Employee(int id, String name, String role, String pin) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.pin = pin;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}
