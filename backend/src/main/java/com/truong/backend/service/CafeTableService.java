package com.truong.backend.service;

import com.truong.backend.entity.CafeTable;
import com.truong.backend.entity.TableStatus;
import com.truong.backend.repository.CafeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CafeTableService {

    @Autowired
    private CafeTableRepository cafeTableRepository;

    // Tạo bàn mới (Admin)
    public CafeTable createTable(String tableNumber, Integer capacity) {
        if (cafeTableRepository.findByTableNumber(tableNumber).isPresent()) {
            throw new IllegalArgumentException("Table number already exists: " + tableNumber);
        }

        CafeTable table = new CafeTable();
        table.setTableNumber(tableNumber);
        table.setCapacity(capacity);
        table.setStatus(TableStatus.AVAILABLE);
        return cafeTableRepository.save(table);
    }

    // Lấy danh sách bàn (Admin, Staff)
    public List<CafeTable> getAllTables() {
        return cafeTableRepository.findAll();
    }

    // Lấy bàn theo ID (Admin, Staff)
    public CafeTable getTableById(Long id) {
        return cafeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Table not found with ID: " + id));
    }

    // Cập nhật thông tin bàn (Admin)
    public CafeTable updateTable(Long id, String tableNumber, Integer capacity, TableStatus status) {
        CafeTable table = cafeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Table not found with ID: " + id));
        table.setTableNumber(tableNumber);
        table.setCapacity(capacity);
        table.setStatus(status);
        return cafeTableRepository.save(table);
    }

    // Xóa bàn (Admin)
    public void deleteTable(Long id) {
        if (!cafeTableRepository.existsById(id)) {
            throw new IllegalArgumentException("Table not found with ID: " + id);
        }
        cafeTableRepository.deleteById(id);
    }

    // Lấy danh sách bàn theo trạng thái (Admin, Staff)
    public List<CafeTable> getTablesByStatus(TableStatus status) {
        return cafeTableRepository.findByStatus(status);
    }
}
