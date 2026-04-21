package com.restaurant.model;

public class ExtraToppingsDecorator extends PriceDecorator {
    private double extraCost;

    public ExtraToppingsDecorator(Sellable item, double extraCost) {
        super(item);
        this.extraCost = extraCost;
    }

    @Override
    public double calculatePrice() {
        return super.calculatePrice() + extraCost;
    }
}

