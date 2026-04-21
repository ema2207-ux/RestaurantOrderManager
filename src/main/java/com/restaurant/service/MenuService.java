package com.restaurant.service;

import com.restaurant.model.MenuItem;
import com.restaurant.model.FoodItem;
import com.restaurant.model.DrinkItem;
import com.restaurant.repository.generic.GenericRepository;
import com.restaurant.config.DatabaseConfig;
import java.sql.*;

public class MenuService extends GenericRepository<MenuItem> {
    private static MenuService instance;

    private MenuService() {}

    public static MenuService getInstance() {
        if (instance == null) { instance = new MenuService(); }
        return instance;
    }

    @Override
    protected String getTableName() { return "menu_items"; }

    @Override
    protected MenuItem mapResultSetToEntity(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        double price = rs.getDouble("base_price");
        String type = rs.getString("item_type");
        if ("FOOD".equals(type)) {
            return new FoodItem(name, price, rs.getBoolean("is_vegan"));
        } else {
            return new DrinkItem(name, price, rs.getDouble("volume"));
        }
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, MenuItem item) throws SQLException {}
    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, MenuItem item) throws SQLException {}

    public void create(String name, double price, String type, Boolean isVegan, Double volume) {
        String sql = "INSERT INTO menu_items (name, base_price, item_type, is_vegan, volume) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setString(3, type);
            if (isVegan != null) pstmt.setBoolean(4, isVegan); else pstmt.setNull(4, Types.BOOLEAN);
            if (volume != null) pstmt.setDouble(5, volume); else pstmt.setNull(5, Types.DOUBLE);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(int id, String name, double price) {
        String sql = "UPDATE menu_items SET name = ?, base_price = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

