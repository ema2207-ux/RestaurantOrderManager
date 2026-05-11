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
            while (rs.next()) {
                results.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
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
            sql += " (order_id, total_amount) VALUES (?, ?)";
        } else {
            return; // Not implemented for other tables yet
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setInsertParameters(pstmt, entity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
