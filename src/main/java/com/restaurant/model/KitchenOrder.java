package com.restaurant.model;

import java.time.LocalDateTime;
import java.util.List;

public class KitchenOrder {
    private int tableId;
    private List<MenuItem> items;
    private LocalDateTime timestamp;
    private boolean completed;
    private String destination; // "BUCATARIE" sau "BAR"

    public KitchenOrder(int tableId, List<MenuItem> items, String destination) {
        this.tableId = tableId;
        this.items = items;
        this.destination = destination;
        this.timestamp = LocalDateTime.now();
        this.completed = false;
    }

    public int getTableId() { return tableId; }
    public List<MenuItem> getItems() { return items; }
    public String getDestination() { return destination; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
