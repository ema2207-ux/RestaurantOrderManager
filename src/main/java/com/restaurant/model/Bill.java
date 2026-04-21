package com.restaurant.model;

import java.time.LocalDateTime;

public class Bill {
    private int id;
    private int orderId;
    private double totalAmount;
    private LocalDateTime issueDate;

    public Bill(int id, int orderId, double totalAmount, LocalDateTime issueDate) {
        this.id = id;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.issueDate = issueDate;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getIssueDate() { return issueDate; }
}

