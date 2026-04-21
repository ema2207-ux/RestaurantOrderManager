package com.restaurant.service;

import com.restaurant.model.Employee;
import com.restaurant.repository.generic.GenericRepository;
import com.restaurant.config.DatabaseConfig;
import java.sql.*;
import java.util.List;

public class EmployeeService extends GenericRepository<Employee> {
    private static EmployeeService instance;

    private EmployeeService() {}

    public static EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeService();
        }
        return instance;
    }

    @Override
    protected String getTableName() { return "employees"; }

    @Override
    protected Employee mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("role"));
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Employee e) throws SQLException {
        pstmt.setString(1, e.getName());
        pstmt.setString(2, e.getRole());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Employee e) throws SQLException {
        pstmt.setString(1, e.getName());
        pstmt.setString(2, e.getRole());
        pstmt.setInt(3, e.getId());
    }

    public void create(String name, String role) {
        String sql = "INSERT INTO employees (name, role) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(int id, String name, String role) {
        String sql = "UPDATE employees SET name = ?, role = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

