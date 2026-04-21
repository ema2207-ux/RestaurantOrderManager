package com.restaurant.service;

import com.restaurant.model.Table;
import com.restaurant.repository.generic.GenericRepository;
import com.restaurant.config.DatabaseConfig;
import java.sql.*;

public class TableService extends GenericRepository<Table> {
    private static TableService instance;

    private TableService() {}

    public static TableService getInstance() {
        if (instance == null) { instance = new TableService(); }
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

    public void create(int id, boolean isOccupied) {
        String sql = "INSERT INTO restaurant_tables (id, is_occupied) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setBoolean(2, isOccupied);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(int id, boolean isOccupied) {
        String sql = "UPDATE restaurant_tables SET is_occupied = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isOccupied);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

