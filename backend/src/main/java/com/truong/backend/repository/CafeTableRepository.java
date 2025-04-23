package com.truong.backend.repository;

import com.truong.backend.entity.CafeTable;
import com.truong.backend.entity.enums.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CafeTableRepository extends JpaRepository<CafeTable, Long> {
    // Tìm bàn theo số bàn
    Optional<CafeTable> findByTableNumber(String tableNumber);

    // Lấy danh sách bàn theo trạng thái (AVAILABLE, RESERVED, OCCUPIED)
    List<CafeTable> findByStatus(TableStatus status);
}