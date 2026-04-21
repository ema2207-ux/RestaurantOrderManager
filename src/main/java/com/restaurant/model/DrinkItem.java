package com.restaurant.model;

public class DrinkItem extends MenuItem {
    private double volume;

    public DrinkItem(String name, double basePrice, double volume) {
        super(name, basePrice);
        this.volume = volume;
    }

    public double getVolume() {
        return volume;
    }
}