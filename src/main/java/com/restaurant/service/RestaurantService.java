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
    private static Map<Integer, List<MenuItem>> persistentOrders = new HashMap<>();
    private static List<KitchenOrder> activeKitchenOrders = new ArrayList<>();

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

    public void sendToKitchen(int tableId, List<MenuItem> items) {
        List<MenuItem> foodItems = new ArrayList<>();
        List<MenuItem> drinkItems = new ArrayList<>();

        for (MenuItem item : items) {
            // Presupunem că FoodItem și DrinkItem sunt clase distincte sau verificăm altfel tipul
            if (item.getClass().getSimpleName().contains("Drink") || item.getName().toLowerCase().contains("apa") || item.getName().toLowerCase().contains("cola") || item.getName().toLowerCase().contains("bere") || item.getName().toLowerCase().contains("vin") || item.getName().toLowerCase().contains("cafea") || item.getName().toLowerCase().contains("fanta") || item.getName().toLowerCase().contains("ceai")) {
                drinkItems.add(item);
            } else {
                foodItems.add(item);
            }
        }

        if (!foodItems.isEmpty()) {
            activeKitchenOrders.add(new KitchenOrder(tableId, foodItems, "BUCATARIE"));
        }
        if (!drinkItems.isEmpty()) {
            activeKitchenOrders.add(new KitchenOrder(tableId, drinkItems, "BAR"));
        }
    }

    public List<KitchenOrder> getActiveKitchenOrders() {
        return activeKitchenOrders;
    }

    public void completeKitchenOrder(KitchenOrder order) {
        order.setCompleted(true);
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

    public int createOrderInDatabase(int tableId, String waiterName) {
        try {
            Employee emp = EmployeeService.getInstance().findByName(waiterName);
            int employeeId = (emp != null) ? emp.getId() : 1; // Default to ID 1 if waiter not found
            
            OrderService.getInstance().create(tableId, employeeId, "CLOSED");
            
            // Retrieve the created order ID from the database
            List<Order> allOrders = OrderService.getInstance().findAll();
            // Get the last order (most recently created)
            if (!allOrders.isEmpty()) {
                Order lastOrder = allOrders.get(allOrders.size() - 1);
                return lastOrder.getId();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] createOrderInDatabase failed: " + e.getMessage());
            e.printStackTrace();
        }
        return 1; // Fallback default
    }

    public Set<MenuItem> getMenu() { return menu; }

    public Collection<Table> getTables() {
        return tables.values();
    }
}
