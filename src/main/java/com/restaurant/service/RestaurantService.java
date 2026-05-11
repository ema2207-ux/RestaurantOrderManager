package com.restaurant.service;

import com.restaurant.model.*;
import com.restaurant.exception.InvalidOrderException;
import com.restaurant.repository.RestaurantRepository;
import java.util.*;

public class RestaurantService {
    private Set<MenuItem> menu = new TreeSet<>();
    private Map<Integer, Table> tables = new HashMap<>();
    private Map<Integer, Employee> employees = new HashMap<>();
    private List<Order> history = new ArrayList<>();
    private RestaurantRepository repository = new RestaurantRepository();
    private static Map<Integer, List<MenuItem>> persistentOrders = new HashMap<>(); // Statica pentru a pastra datele intre sesiuni de login

    public RestaurantService() {
        loadData();
    }

    public void loadData() {
        menu.clear();
        menu.addAll(repository.getAllMenuItems());

        tables.clear();
        for (Table t : repository.getAllTables()) {
            tables.put(t.getId(), t);
        }

        employees.clear();
        for (Employee e : repository.getAllEmployees()) {
            employees.put(e.getId(), e);
        }
    }

    public void addMenuItem(MenuItem item) { menu.add(item); }

    public void addTable(Table table) { tables.put(table.getId(), table); }

    public void createOrder(int tableId) throws InvalidOrderException {
        Table table = tables.get(tableId);
        if (table == null) throw new InvalidOrderException("Masa nu exista!");
        if (table.isOccupied()) throw new InvalidOrderException("Masa e deja ocupata!");

        table.setOccupied(true);
        history.add(new Order(tableId));
        System.out.println("Comanda creata pentru masa " + tableId);
    }

    public void addItemToTable(int tableId, MenuItem item) {
        persistentOrders.computeIfAbsent(tableId, k -> new ArrayList<>()).add(item);
    }

    public List<MenuItem> getOrderItems(int tableId) {
        return persistentOrders.getOrDefault(tableId, new ArrayList<>());
    }

    public double calculateTableTotal(int tableId) {
        return getOrderItems(tableId).stream()
                .mapToDouble(Sellable::calculatePrice)
                .sum();
    }

    public void clearTableOrder(int tableId) {
        persistentOrders.remove(tableId);
    }

    public Set<MenuItem> getMenu() { return menu; }

    public Collection<Table> getTables() {
        return tables.values();
    }
}
