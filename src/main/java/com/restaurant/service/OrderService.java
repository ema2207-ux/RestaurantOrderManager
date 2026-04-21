package com.restaurant.service;

import com.restaurant.model.Order;
import com.restaurant.repository.generic.GenericRepository;
import com.restaurant.config.DatabaseConfig;
import java.sql.*;

public class OrderService extends GenericRepository<Order> {
    private static OrderService instance;

    private OrderService() {}

    public static OrderService getInstance() {
        if (instance == null) { instance = new OrderService(); }
        return instance;
    }

    @Override
    protected String getTableName() { return "orders"; }

    @Override
    protected Order mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Order(rs.getInt("id"), rs.getInt("table_id"), rs.getInt("employee_id"), rs.getString("status"));
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Order o) throws SQLException {}
    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Order o) throws SQLException {}

    public void create(int tableId, int employeeId, String status) {
        String sql = "INSERT INTO orders (table_id, employee_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tableId);
            pstmt.setInt(2, employeeId);
            pstmt.setString(3, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

