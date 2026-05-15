package com.restaurant.repository.generic;

import com.restaurant.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class GenericRepository<T> {

    protected abstract String getTableName();
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract void setInsertParameters(PreparedStatement pstmt, T entity) throws SQLException;
    protected abstract void setUpdateParameters(PreparedStatement pstmt, T entity) throws SQLException;

    public List<T> findAll() {
        List<T> results = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("[DEBUG] Executing SQL: " + sql);
            while (rs.next()) {
                System.out.println("[DEBUG] Found row in " + getTableName());
                results.add(mapResultSetToEntity(rs));
            }
            System.out.println("[DEBUG] Total rows retrieved from " + getTableName() + ": " + results.size());
        } catch (SQLException e) {
            System.err.println("[REPO ERROR] Failed to find all in " + getTableName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    public T findById(int id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(int id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(T entity) {
        // We need different logic for different tables, but a common pattern is:
        String sql = "INSERT INTO " + getTableName();
        if ("employees".equals(getTableName())) {
            sql += " (name, role, pin) VALUES (?, ?, ?)";
        } else if ("restaurant_tables".equals(getTableName())) {
            sql += " (id, is_occupied) VALUES (?, ?)";
        } else if ("menu_items".equals(getTableName())) {
            sql += " (name, base_price) VALUES (?, ?)";
        } else if ("orders".equals(getTableName())) {
            sql += " (table_id, employee_id, status) VALUES (?, ?, ?)";
        } else if ("bills".equals(getTableName())) {
            sql += " (order_id, total_amount, payment_method, employee_name) VALUES (?, ?, ?, ?)";
        } else {
            return; // Not implemented for other tables yet
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setInsertParameters(pstmt, entity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // If it's a bills insert and we get "column does not exist", try without the new columns
            if ("bills".equals(getTableName()) && e.getMessage().contains("does not exist")) {
                System.err.println("[REPO FALLBACK] Tentativa insert fără coloane noi...");
                String fallbackSql = "INSERT INTO bills (order_id, total_amount) VALUES (?, ?)";
                try (Connection conn = DatabaseConfig.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(fallbackSql)) {
                    if (entity instanceof com.restaurant.model.Bill) {
                        com.restaurant.model.Bill bill = (com.restaurant.model.Bill) entity;
                        pstmt.setInt(1, bill.getOrderId());
                        pstmt.setDouble(2, bill.getAmount());
                        pstmt.executeUpdate();
                        System.out.println("[REPO FALLBACK] Insert reușit (fără payment_method și employee_name)");
                    }
                } catch (SQLException fallbackEx) {
                    System.err.println("[REPO FALLBACK ERROR] " + fallbackEx.getMessage());
                    fallbackEx.printStackTrace();
                }
            } else {
                System.err.println("[REPO ERROR] Failed to save entity: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void update(T entity) {
        String sql = "UPDATE " + getTableName();
        if ("employees".equals(getTableName())) {
            sql += " SET name = ?, role = ?, pin = ? WHERE id = ?";
        } else if ("restaurant_tables".equals(getTableName())) {
            sql += " SET is_occupied = ? WHERE id = ?";
        } else if ("orders".equals(getTableName())) {
            sql += " SET status = ? WHERE id = ?";
        } else {
            return;
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setUpdateParameters(pstmt, entity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
