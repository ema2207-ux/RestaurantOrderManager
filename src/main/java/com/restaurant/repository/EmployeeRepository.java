package com.restaurant.repository;

import com.restaurant.model.Employee;
import com.restaurant.repository.generic.GenericRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeRepository extends GenericRepository<Employee> {
    private static EmployeeRepository instance;

    private EmployeeRepository() {}

    public static EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepository();
        }
        return instance;
    }

    @Override
    protected String getTableName() { return "employees"; }

    @Override
    protected Employee mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Employee(rs.getInt("id"), rs.getString("name"), rs.getString("role"), rs.getString("pin"));
    }

    @Override
    protected void setInsertParameters(PreparedStatement pstmt, Employee e) throws SQLException {
        pstmt.setString(1, e.getName());
        pstmt.setString(2, e.getRole());
        pstmt.setString(3, e.getPin());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement pstmt, Employee e) throws SQLException {
        pstmt.setString(1, e.getName());
        pstmt.setString(2, e.getRole());
        pstmt.setString(3, e.getPin());
        pstmt.setInt(4, e.getId());
    }
}
