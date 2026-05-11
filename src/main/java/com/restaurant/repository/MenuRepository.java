package com.restaurant.repository;

import com.restaurant.model.DrinkItem;
import com.restaurant.model.FoodItem;
import com.restaurant.model.MenuItem;
import com.restaurant.repository.generic.GenericRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuRepository extends GenericRepository<MenuItem> {
    private static MenuRepository instance;

    private MenuRepository() {}

    public static MenuRepository getInstance() {
        if (instance == null) {
            instance = new MenuRepository();
        }
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
    protected void setInsertParameters(PreparedStatement pstmt, MenuItem item) throws SQLException {
        pstmt.setString(1, item.getName());
        pstmt.setDouble(2, item.getBasePrice());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, MenuItem item) throws SQLException {}
}
