package com.restaurant.repository;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("base_price");
                String type = rs.getString("item_type");
                if ("FOOD".equals(type)) {
                    items.add(new FoodItem(name, price, rs.getBoolean("is_vegan")));
                } else {
                    items.add(new DrinkItem(name, price, rs.getDouble("volume")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Table> getAllTables() {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT * FROM restaurant_tables";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tables.add(new Table(rs.getInt("id"), rs.getBoolean("is_occupied")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("role")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }
}

