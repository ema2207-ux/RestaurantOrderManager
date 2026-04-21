package com.restaurant.model;

public class OrderItem {
    private int orderId;
    private int menuItemId;

    public OrderItem(int orderId, int menuItemId) {
        this.orderId = orderId;
        this.menuItemId = menuItemId;
    }

    public int getOrderId() { return orderId; }
    public int getMenuItemId() { return menuItemId; }
}

