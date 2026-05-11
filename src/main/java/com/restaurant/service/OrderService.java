package com.restaurant.service;

import com.restaurant.model.Order;
import com.restaurant.repository.OrderRepository;
import java.util.List;

public class OrderService {
    private static OrderService instance;
    private final OrderRepository orderRepository = OrderRepository.getInstance();

    private OrderService() {}

    public static OrderService getInstance() {
        if (instance == null) { instance = new OrderService(); }
        return instance;
    }

    public void create(int tableId, int employeeId, String status) {
        Order o = new Order(tableId);
        o.setEmployeeId(employeeId);
        o.setStatus(status);
        orderRepository.save(o);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public void updateStatus(int id, String status) {
        Order o = orderRepository.findById(id);
        if (o != null) {
            o.setStatus(status);
            orderRepository.update(o);
        }
    }
}

