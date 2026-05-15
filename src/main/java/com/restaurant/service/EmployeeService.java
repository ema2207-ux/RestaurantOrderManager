package com.restaurant.service;

import com.restaurant.model.Employee;
import com.restaurant.repository.EmployeeRepository;
import java.util.List;

public class EmployeeService {
    private static EmployeeService instance;
    private final EmployeeRepository employeeRepository = EmployeeRepository.getInstance();

    private EmployeeService() {}

    public static EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeService();
        }
        return instance;
    }

    public void create(String name, String role, String pin) {
        Employee e = new Employee(0, name, role, pin);
        employeeRepository.save(e);
    }

    public void update(int id, String name, String role, String pin) {
        Employee e = new Employee(id, name, role, pin);
        employeeRepository.update(e);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee login(String pin) {
        return findAll().stream()
                .filter(e -> pin.equals(e.getPin()))
                .findFirst()
                .orElse(null);
    }

    public Employee findByName(String name) {
        return findAll().stream()
                .filter(e -> name.equals(e.getName()))
                .findFirst()
                .orElse(null);
    }

    public void delete(int id) {
        employeeRepository.delete(id);
    }
}
