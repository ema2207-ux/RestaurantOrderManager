package com.restaurant.service;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.model.DrinkItem;
import com.restaurant.model.FoodItem;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSeeder {

    public static void seed() {
        seedMenuItems();
        seedEmployees();
    }

    private static void seedMenuItems() {
        if (!isTableEmpty("menu_items")) {
            System.out.println("Tabela menu_items nu este goală. Se sărește popularea.");
            return;
        }

        System.out.println("Populăm meniul...");

        // Soups
        addFoodItem("Ciorbă de burtă", 18.0, false);
        addFoodItem("Ciorbă de văcuță", 17.0, false);
        addFoodItem("Ciorbă de legume", 15.0, true);
        addFoodItem("Ciorbă rădăuțeană", 18.0, false);

        // Main Courses
        addFoodItem("Tochitură moldovenească", 35.0, false);
        addFoodItem("Păstrăv la grătar", 42.0, false);
        addFoodItem("Mici cu muștar (4 buc)", 25.0, false);
        addFoodItem("Șnițel de pui", 28.0, false);
        addFoodItem("Sărmăluțe cu mămăliguță", 32.0, false);
        addFoodItem("Tocăniță de hribi", 38.0, true);

        // Desserts
        addFoodItem("Papanași cu smântână și dulceatță", 22.0, false);
        addFoodItem("Clătite cu ciocolată", 15.0, false);
        addFoodItem("Plăcintă cu mere", 12.0, true);

        // Drinks
        addDrinkItem("Apă plată 500ml", 8.0, 0.5);
        addDrinkItem("Apă minerală 500ml", 8.0, 0.5);
        addDrinkItem("Coca Cola 330ml", 9.0, 0.33);
        addDrinkItem("Fanta 330ml", 9.0, 0.33);
        addDrinkItem("Bere Ursus 500ml", 12.0, 0.5);
        addDrinkItem("Vinul Casei (pahara)", 15.0, 0.2);
        addDrinkItem("Cafea Espresso", 10.0, 0.05);
        addDrinkItem("Ceai cald", 8.0, 0.25);
    }

    private static void seedEmployees() {
        if (!isTableEmpty("employees")) {
             System.out.println("Tabela employees nu este goală. Se sărește popularea.");
             return;
        }

        System.out.println("Populăm angajații...");
        addEmployee("Radulescu Emanuel-Andrei", "Manager", "0000");
        addEmployee("Maria Ionescu", "Chelner", "1111");
        addEmployee("Ion Dumitru", "Chelner", "2222");
        addEmployee("Elena Vasile", "Chelner", "3333");
    }

    private static void addFoodItem(String name, double price, boolean isVegan) {
        String sql = "INSERT INTO menu_items (name, base_price, item_type, is_vegan) VALUES (?, ?, 'FOOD', ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setBoolean(3, isVegan);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Eroare la adăugarea produsului alimentar: " + e.getMessage());
        }
    }

    private static void addDrinkItem(String name, double price, double volume) {
        String sql = "INSERT INTO menu_items (name, base_price, item_type, volume) VALUES (?, ?, 'DRINK', ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setDouble(3, volume);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Eroare la adăugarea băuturii: " + e.getMessage());
        }
    }

    private static void addEmployee(String name, String role, String pin) {
        String sql = "INSERT INTO employees (name, role, pin) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setString(3, pin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Eroare la adăugarea angajatului: " + e.getMessage());
        }
    }

    private static boolean isTableEmpty(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("Eroare la verificarea tabelei " + tableName + ": " + e.getMessage());
        }
        return false;
    }
}
