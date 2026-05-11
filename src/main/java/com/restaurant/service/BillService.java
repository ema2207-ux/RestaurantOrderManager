package com.restaurant.service;

import com.restaurant.model.Bill;
import com.restaurant.repository.BillRepository;

public class BillService {
    private static BillService instance;
    private final BillRepository billRepository = BillRepository.getInstance();

    private BillService() {}

    public static BillService getInstance() {
        if (instance == null) { instance = new BillService(); }
        return instance;
    }

    public void create(int orderId, double amount) {
        Bill bill = new Bill(0, orderId, amount, null);
        billRepository.save(bill);
    }
}

