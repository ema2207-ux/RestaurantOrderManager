package com.restaurant.service;

import com.restaurant.model.OrderItem;
import com.restaurant.repository.OrderItemRepository;
import java.util.List;

public class OrderItemService {
    private static OrderItemService instance;
    private final OrderItemRepository orderItemRepository = OrderItemRepository.getInstance();

    private OrderItemService() {}

    public static OrderItemService getInstance() {
        if (instance == null) { instance = new OrderItemService(); }
        return instance;
    }

    public void create(int orderId, int menuItemId) {
        OrderItem oi = new OrderItem(orderId, menuItemId);
        orderItemRepository.save(oi);
    }

    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }
}

