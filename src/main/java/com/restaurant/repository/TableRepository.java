package com.restaurant.repository;

import com.restaurant.model.Table;
import com.restaurant.repository.generic.GenericRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TableRepository extends GenericRepository<Table> {
    private static TableRepository instance;

    private TableRepository() {}

    public static TableRepository getInstance() {
        if (instance == null) {
            instance = new TableRepository();
        }
        return instance;
    }

    @Override
    protected String getTableName() { return "restaurant_tables"; }

    @Override
    protected Table mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Table(rs.getInt("id"), rs.getBoolean("is_occupied"));
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Table t) throws SQLException {
        pstmt.setInt(1, t.getId());
        pstmt.setBoolean(2, t.isOccupied());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Table t) throws SQLException {
        pstmt.setBoolean(1, t.isOccupied());
        pstmt.setInt(2, t.getId());
    }
}
