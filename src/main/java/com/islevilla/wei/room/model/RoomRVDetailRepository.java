package com.islevilla.wei.room.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RoomRVDetailRepository extends JpaRepository<RoomRVDetail, Integer> {
    // 傳進整個RoomRVOrder entity
    List<RoomRVDetail> findByRoomRVOrder(RoomRVOrder roomRVOrder);
    
    // 接駁預約：查詢每筆訂房訂單的總入住人數
    @Query("SELECT COALESCE(SUM(r.guestCount), 0) FROM RoomRVDetail r WHERE r.roomRVOrder = :roomRVOrder")
    Integer countGuestByRoomRVOrder(@Param("roomRVOrder") RoomRVOrder roomRVOrder);
}
