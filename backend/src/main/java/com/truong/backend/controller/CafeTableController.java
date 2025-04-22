package com.truong.backend.controller;

import com.truong.backend.dto.ApiResponse;
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
    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<List<CafeTable>>> getTablesByStatus(@RequestParam(required = false) TableStatus status) {
        if (status != null) {
            List<CafeTable> cafeTable = cafeTableService.getTablesByStatus(status);
            return ResponseEntity.ok(
                    new ApiResponse<>("success","Get success", cafeTable)
            );
        }
        return ResponseEntity.ok(
                new ApiResponse<>("success","Get success", cafeTableService.getAllTables())
        );
    }

    // Lấy bàn theo ID (Admin, Staff)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<CafeTable>> getTableById(@PathVariable Long id) {
        CafeTable cafeTable = cafeTableService.getTableById(id);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Get table successful", cafeTable)
        );
    }

    // Tạo bàn mới (Admin)
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<CafeTable>> createTable(@RequestBody CafeTableRequest request) {
        CafeTable cafeTable= cafeTableService.createTable(request);

        return ResponseEntity.ok(
                new ApiResponse<>("success", "Create table successful", cafeTable)
        );
    }
    // Cập nhật thông tin bàn (Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<CafeTable>> updateTable(
            @PathVariable Long id,
            @RequestBody CafeTableRequest request
    ) {

        CafeTable cafeTable = cafeTableService.updateTable(id, request);
        return ResponseEntity.ok(
               new ApiResponse<>("success", "Update table successful", cafeTable)
        );
    }


    // Xóa bàn (Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteTable(@PathVariable Long id) {
        cafeTableService.deleteTable(id);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Table deleted",null)
        );
    }
}
