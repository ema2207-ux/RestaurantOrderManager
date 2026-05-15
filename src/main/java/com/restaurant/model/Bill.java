package com.restaurant.model;

import java.time.LocalDateTime;

public class Bill {
    private int id;
    private int orderId;
    private double amount;
    private String paymentMethod;
    private String employeeName;

    public Bill(int id, int orderId, double amount, String paymentMethod) {
        this(id, orderId, amount, paymentMethod, "Manager");
    }

    public Bill(int id, int orderId, double amount, String paymentMethod, String employeeName) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.employeeName = employeeName;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getEmployeeName() { return employeeName; }
}
