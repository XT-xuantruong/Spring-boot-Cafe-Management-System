package com.truong.backend.service;

import com.truong.backend.dto.request.CafeTableRequestDTO;
import com.truong.backend.entity.CafeTable;
import com.truong.backend.entity.enums.TableStatus;
import com.truong.backend.repository.CafeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CafeTableService {

    @Autowired
    private CafeTableRepository cafeTableRepository;

    // Lấy danh sách bàn (Admin, Staff)
    public List<CafeTable> getAllTables() {
        return cafeTableRepository.findAll();
    }

    // Lấy bàn theo ID (Admin, Staff)
    public CafeTable getTableById(Long id) {
        return cafeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Table not found with ID: " + id));
    }
    // Tạo bàn mới (Admin)
    public CafeTable createTable(
            CafeTableRequestDTO request
    ) {
        if (
            cafeTableRepository.findByTableNumber(request.getTableNumber()).isPresent()
        ) {
            throw new IllegalArgumentException("Table number already exists: " + request.getTableNumber());
        }

        CafeTable table = new CafeTable();
        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setStatus(TableStatus.AVAILABLE);
        return cafeTableRepository.save(table);
    }

    // Cập nhật thông tin bàn (Admin)
    public CafeTable updateTable(
            Long id,
            CafeTableRequestDTO request
    ) {
        CafeTable table = cafeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Table not found with ID: " + id));
        if (
            request.getTableNumber() != null &&
            !request.getTableNumber().equals(table.getTableNumber())
        ) {
            // Kiểm tra trùng số bàn nếu thay đổi
            if (cafeTableRepository.findByTableNumber(request.getTableNumber()).isPresent()) {
                throw new IllegalArgumentException("Table number already exists: " + request.getTableNumber());
            }
            table.setTableNumber(request.getTableNumber());
        }

        if (request.getCapacity() != null) {
            table.setCapacity(request.getCapacity());
        }

        if (request.getStatus() != null) {
            table.setStatus(request.getStatus());
        }

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

    public CafeTable updateTableStatus(Long id, TableStatus request) {
        CafeTable table = cafeTableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Table not found with ID: " + id));
        if (request != null) {
            table.setStatus(request);
        }
        return cafeTableRepository.save(table);
    }
}
