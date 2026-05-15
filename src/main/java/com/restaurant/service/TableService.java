package com.restaurant.service;

import com.restaurant.model.Table;
import com.restaurant.repository.TableRepository;
import java.util.List;

public class TableService {
    private static TableService instance;
    private final TableRepository tableRepository = TableRepository.getInstance();

    private TableService() {}

    public static TableService getInstance() {
        if (instance == null) { instance = new TableService(); }
        return instance;
    }

    public void create(int id, boolean isOccupied) {
        Table t = new Table(id, isOccupied);
        tableRepository.save(t);
    }

    public void update(int id, boolean isOccupied) {
        Table t = new Table(id, isOccupied);
        tableRepository.update(t);
    }

    public List<Table> findAll() {
        List<Table> tables = tableRepository.findAll();
        tables.sort((t1, t2) -> Integer.compare(t1.getId(), t2.getId()));
        return tables;
    }

    public void resetAllTables() {
        List<Table> tables = findAll();
        for (Table t : tables) {
            if (t.isOccupied()) {
                update(t.getId(), false);
            }
        }
    }
}
