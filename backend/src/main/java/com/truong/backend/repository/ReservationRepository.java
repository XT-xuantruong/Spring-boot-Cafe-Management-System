package com.truong.backend.repository;

import com.truong.backend.entity.Reservation;
import com.truong.backend.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Lấy danh sách đặt bàn của một khách hàng
    List<Reservation> findByUserId(Long userId);

    // Lấy danh sách đặt bàn theo trạng thái
    List<Reservation> findByStatus(ReservationStatus status);

    // Kiểm tra bàn có bị trùng lịch đặt không
    @Query("SELECT r FROM Reservation r WHERE r.cafeTable.tableId = :tableId AND r.reservationTime = :reservationTime AND r.status != 'CANCELLED'")
    List<Reservation> findByTableIdAndReservationTime(Long tableId, LocalDateTime reservationTime);
}
