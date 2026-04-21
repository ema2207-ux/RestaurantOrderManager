package com.restaurant;

import com.restaurant.model.*;
import com.restaurant.service.RestaurantService;
import com.restaurant.exception.InvalidOrderException;

public class Main {
    public static void main(String[] args) {
        // Inițializăm serviciul principal al aplicației
        RestaurantService service = new RestaurantService();

        // 1. Populăm meniul cu diferite produse (Testăm Polimorfismul)
        service.addMenuItem(new FoodItem("Pizza Margherita", 35.0, false));
        service.addMenuItem(new FoodItem("Salată Vegană", 25.0, true));
        service.addMenuItem(new DrinkItem("Limonadă", 15.0, 0.5));
        service.addMenuItem(new DrinkItem("Apă Plată", 10.0, 0.5));

        // 2. Afișăm meniul pentru a vedea sortarea automată
        // Deoarece am folosit TreeSet și am implementat Comparable,
        // produsele se vor afișa de la cel mai ieftin la cel mai scump!
        System.out.println("=== Meniul Restaurantului (Sortat după preț) ===");
        for (MenuItem item : service.getMenu()) {
            System.out.println(item.getName() + " - " + item.calculatePrice() + " RON");
        }
        System.out.println("===============================================\n");

        // 3. Adăugăm mese în sistem
        service.addTable(new Table(1));
        service.addTable(new Table(2));

        // 4. Testăm scenariile de succes și excepțiile (try-catch)
        try {
            System.out.println("--> Încercăm să deschidem o comandă la Masa 1...");
            service.createOrder(1); // Succes

            System.out.println("--> Încercăm să deschidem din nou o comandă la Masa 1...");
            service.createOrder(1); // Va arunca eroare: Masa e deja ocupată!

        } catch (InvalidOrderException e) {
            System.err.println("EROARE CUSTOM: " + e.getMessage());
        }

        try {
            System.out.println("\n--> Încercăm să deschidem o comandă la Masa 99...");
            service.createOrder(99); // Va arunca eroare: Masa nu există!

        } catch (InvalidOrderException e) {
            System.err.println("EROARE CUSTOM: " + e.getMessage());
        }
    }
}