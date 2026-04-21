package com.restaurant.model;

public class Table {
    private int id;
    private boolean isOccupied;

    public Table(int id) {
        this.id = id;
        this.isOccupied = false;
    }

    public Table(int id, boolean isOccupied) {
        this.id = id;
        this.isOccupied = isOccupied;
    }

    public int getId() { return id; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }
}