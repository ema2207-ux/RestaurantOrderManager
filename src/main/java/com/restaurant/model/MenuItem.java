package com.restaurant.model;

public abstract class MenuItem implements Sellable, Comparable<MenuItem> {
    private String name;
    private double basePrice;

    // Builder Pattern pentru MenuItem
    public static class Builder {
        private String name;
        private double basePrice;
        private boolean isVegan;
        private double volume;
        private String type;

        public Builder setName(String name) { this.name = name; return this; }
        public Builder setBasePrice(double price) { this.basePrice = price; return this; }
        public Builder setVegan(boolean vegan) { this.isVegan = vegan; this.type = "FOOD"; return this; }
        public Builder setVolume(double volume) { this.volume = volume; this.type = "DRINK"; return this; }

        public MenuItem build() {
            if ("FOOD".equals(type)) {
                return new FoodItem(name, basePrice, isVegan);
            } else {
                return new DrinkItem(name, basePrice, volume);
            }
        }
    }

    public MenuItem(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }

    @Override
    public double calculatePrice() { return basePrice; }

    @Override
    public int compareTo(MenuItem other) {
        return Double.compare(this.basePrice, other.basePrice);
    }
}