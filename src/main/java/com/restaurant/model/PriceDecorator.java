package com.restaurant.model;

public abstract class PriceDecorator implements Sellable {
    protected Sellable item;

    public PriceDecorator(Sellable item) {
        this.item = item;
    }

    @Override
    public double calculatePrice() {
        return item.calculatePrice();
    }
}

