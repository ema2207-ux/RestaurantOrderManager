package com.restaurant.repository;

import com.restaurant.model.Bill;
import com.restaurant.repository.generic.GenericRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillRepository extends GenericRepository<Bill> {
    private static BillRepository instance;

    private BillRepository() {}

    public static BillRepository getInstance() {
        if (instance == null) {
            instance = new BillRepository();
        }
        return instance;
    }

    @Override
    protected String getTableName() { return "bills"; }

    @Override
    protected Bill mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Bill(rs.getInt("id"), rs.getInt("order_id"), rs.getDouble("total_amount"), rs.getTimestamp("issue_date").toLocalDateTime());
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Bill b) throws SQLException {
        pstmt.setInt(1, b.getOrderId());
        pstmt.setDouble(2, b.getTotalAmount());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Bill b) throws SQLException {}
}
