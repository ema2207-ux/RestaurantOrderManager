package com.restaurant.service;

import com.restaurant.model.Bill;
import com.restaurant.repository.generic.GenericRepository;
import com.restaurant.config.DatabaseConfig;
import java.sql.*;

public class BillService extends GenericRepository<Bill> {
    private static BillService instance;

    private BillService() {}

    public static BillService getInstance() {
        if (instance == null) { instance = new BillService(); }
        return instance;
    }

    @Override
    protected String getTableName() { return "bills"; }

    @Override
    protected Bill mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Bill(rs.getInt("id"), rs.getInt("order_id"), rs.getDouble("total_amount"), rs.getTimestamp("issue_date").toLocalDateTime());
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Bill b) throws SQLException {}
    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Bill b) throws SQLException {}

    public void create(int orderId, double amount) {
        String sql = "INSERT INTO bills (order_id, total_amount) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setDouble(2, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

