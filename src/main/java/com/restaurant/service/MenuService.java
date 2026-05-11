package com.restaurant.service;

import com.restaurant.model.MenuItem;
import com.restaurant.model.FoodItem;
import com.restaurant.model.DrinkItem;
import com.restaurant.repository.MenuRepository;
import java.util.List;

public class MenuService {
    private static MenuService instance;
    private final MenuRepository menuRepository = MenuRepository.getInstance();

    private MenuService() {}

    public static MenuService getInstance() {
        if (instance == null) { instance = new MenuService(); }
        return instance;
    }

    public void create(String name, double price, String type, Boolean isVegan, Double volume) {
        MenuItem item;
        if ("FOOD".equals(type)) {
            item = new FoodItem(name, price, isVegan);
        } else {
            item = new DrinkItem(name, price, volume);
        }
        menuRepository.save(item);
    }

    public List<MenuItem> findAll() {
        return menuRepository.findAll();
    }

    public void update(int id, String name, double price) {
        MenuItem item = menuRepository.findById(id);
        if (item != null) {
            // Logica pentru update
            menuRepository.update(item);
        }
    }
}

