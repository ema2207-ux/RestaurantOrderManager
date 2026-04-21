package com.restaurant.model;

public class FoodItem extends MenuItem {
    private boolean isVegan;

    public FoodItem(String name, double basePrice, boolean isVegan) {
        super(name, basePrice);
        this.isVegan = isVegan;
    }

    public boolean isVegan() {
        return isVegan;
    }
}