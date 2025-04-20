package com.truong.backend.controller;

import com.truong.backend.dto.CafeTableRequest;
import com.truong.backend.entity.CafeTable;
import com.truong.backend.entity.TableStatus;
import com.truong.backend.service.CafeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class CafeTableController {

    @Autowired
    private CafeTableService cafeTableService;

    // Lấy danh sách bàn (Admin, Staff)
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<CafeTable>> getAllTables() {
        return ResponseEntity.ok(cafeTableService.getAllTables());
    }

    // Lấy bàn theo ID (Admin, Staff)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<CafeTable> getTableById(@PathVariable Long id) {
        return ResponseEntity.ok(cafeTableService.getTableById(id));
    }

    // Tạo bàn mới (Admin)
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CafeTable> createTable(@RequestBody CafeTableRequest request) {
        return ResponseEntity.ok(
                cafeTableService.createTable(request.getTableNumber(), request.getCapacity())
        );
    }
    // Cập nhật thông tin bàn (Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CafeTable> updateTable(
            @PathVariable Long id,
            @RequestBody CafeTableRequest request) {
        return ResponseEntity.ok(
                cafeTableService.updateTable(id, request.getTableNumber(), request.getCapacity(), request.getStatus())
        );
    }


    // Xóa bàn (Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteTable(@PathVariable Long id) {
        cafeTableService.deleteTable(id);
        return ResponseEntity.ok("Table deleted");
    }
}
