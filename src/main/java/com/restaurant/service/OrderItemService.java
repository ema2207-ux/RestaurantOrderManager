package com.restaurant.service;

import com.restaurant.model.OrderItem;
import com.restaurant.repository.generic.GenericRepository;
import com.restaurant.config.DatabaseConfig;
import java.sql.*;

public class OrderItemService extends GenericRepository<OrderItem> {
    private static OrderItemService instance;

    private OrderItemService() {}

    public static OrderItemService getInstance() {
        if (instance == null) { instance = new OrderItemService(); }
        return instance;
    }

    @Override
    protected String getTableName() { return "order_items"; }

    @Override
    protected OrderItem mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new OrderItem(rs.getInt("order_id"), rs.getInt("menu_item_id"));
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, OrderItem oi) throws SQLException {}
    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, OrderItem oi) throws SQLException {}

    public void create(int orderId, int menuItemId) {
        String sql = "INSERT INTO order_items (order_id, menu_item_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, menuItemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

