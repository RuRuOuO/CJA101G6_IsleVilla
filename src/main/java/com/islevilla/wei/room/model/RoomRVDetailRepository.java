package com.islevilla.wei.room.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface RoomRVDetailRepository extends JpaRepository<RoomRVDetail, Integer> {
    // 傳進整個RoomRVOrder entity
    List<RoomRVDetail> findByRoomRVOrder(RoomRVOrder roomRVOrder);
    
    // 接駁預約：查詢每筆訂房訂單的總入住人數
    @Query("SELECT COALESCE(SUM(r.guestCount), 0) FROM RoomRVDetail r WHERE r.roomRVOrder = :roomRVOrder")
    Integer countGuestByRoomRVOrder(@Param("roomRVOrder") RoomRVOrder roomRVOrder);

    // ==================== 給 RoomTypeAvailability 庫存計算相關查詢 ====================

    //查詢特定房型在特定日期的預訂詳情
    @Query("SELECT rrd FROM RoomRVDetail rrd " +
           "JOIN rrd.roomRVOrder rro " +
           "WHERE rro.checkInDate <= :targetDate AND rro.checkOutDate > :targetDate " +
           "AND rro.roomOrderStatus IN (0, 1, 2) " +
           "AND rrd.roomType.roomTypeId = :roomTypeId")
    List<RoomRVDetail> findReservedDetailsOnDate(
        @Param("roomTypeId") Integer roomTypeId, 
        @Param("targetDate") LocalDate targetDate
    );
    
    /**
     * 查詢特定日期範圍內特定房型的所有預訂詳情
     */
    @Query("SELECT rrd FROM RoomRVDetail rrd " +
           "JOIN rrd.roomRVOrder rro " +
           "WHERE NOT (rro.checkOutDate <= :startDate OR rro.checkInDate >= :endDate) " +
           "AND rro.roomOrderStatus IN (0, 1, 2) " +
           "AND rrd.roomType.roomTypeId = :roomTypeId")
    List<RoomRVDetail> findReservedDetailsInDateRange(
        @Param("roomTypeId") Integer roomTypeId,
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
 // ==================== 給 RoomTypeAvailability 庫存計算相關查詢 ====================
}
