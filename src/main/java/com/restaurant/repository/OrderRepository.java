package com.restaurant.repository;

import com.restaurant.model.Order;
import com.restaurant.repository.generic.GenericRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRepository extends GenericRepository<Order> {
    private static OrderRepository instance;

    private OrderRepository() {}

    public static OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    @Override
    protected String getTableName() { return "orders"; }

    @Override
    protected Order mapResultSetToEntity(ResultSet rs) throws SQLException {
        Order order = new Order(rs.getInt("table_id"));
        order.setId(rs.getInt("id"));
        order.setEmployeeId(rs.getInt("employee_id"));
        order.setStatus(rs.getString("status"));
        return order;
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Order o) throws SQLException {
        pstmt.setInt(1, o.getTableId());
        pstmt.setInt(2, o.getEmployeeId());
        pstmt.setString(3, o.getStatus());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Order o) throws SQLException {
        pstmt.setString(1, o.getStatus());
        pstmt.setInt(2, o.getId());
    }
}
