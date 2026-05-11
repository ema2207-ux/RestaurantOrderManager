package com.restaurant.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int tableId;
    private int employeeId;
    private String status;
    private List<MenuItem> items = new ArrayList<>(); // Aici folosim a treia colecție cerută (List)

    public Order(int tableId) {
        this.tableId = tableId;
        this.status = "OPEN";
    }

    public Order(int id, int tableId, int employeeId, String status) {
        this.id = id;
        this.tableId = tableId;
        this.employeeId = employeeId;
        this.status = status;
    }

    public void addItem(MenuItem item) {
        items.add(item);
    }

    // Getters
    public int getId() { return id; }
    public int getTableId() { return tableId; }
    public int getEmployeeId() { return employeeId; }
    public String getStatus() { return status; }
    public List<MenuItem> getItems() { return items; }

    public void setId(int id) { this.id = id; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public void setStatus(String status) { this.status = status; }
}