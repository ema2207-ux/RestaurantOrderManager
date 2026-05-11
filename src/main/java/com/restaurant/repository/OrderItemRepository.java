package com.restaurant.repository;

import com.restaurant.model.OrderItem;
import com.restaurant.repository.generic.GenericRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemRepository extends GenericRepository<OrderItem> {
    private static OrderItemRepository instance;

    private OrderItemRepository() {}

    public static OrderItemRepository getInstance() {
        if (instance == null) {
            instance = new OrderItemRepository();
        }
        return instance;
    }

    @Override
    protected String getTableName() { return "order_items"; }

    @Override
    protected OrderItem mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new OrderItem(rs.getInt("order_id"), rs.getInt("menu_item_id"));
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, OrderItem oi) throws SQLException {
        pstmt.setInt(1, oi.getOrderId());
        pstmt.setInt(2, oi.getMenuItemId());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, OrderItem oi) throws SQLException {}
}
