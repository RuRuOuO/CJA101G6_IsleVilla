package com.islevilla.patty.booking.model;

import com.islevilla.wei.room.model.RoomRVDetail;
import com.islevilla.wei.room.model.RoomRVOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<RoomRVOrder, Integer> {

    /**
     * 找出在指定日期區間內有重疊的訂單
     */
    @Query("SELECT o FROM RoomRVOrder o WHERE o.checkInDate < :endDate AND o.checkOutDate > :startDate")
    List<RoomRVOrder> findOverlappingOrders(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 根據訂單列表，找出所有的訂單明細
     */
    @Query("SELECT d FROM RoomRVDetail d WHERE d.roomRVOrder IN :orders")
    List<RoomRVDetail> findDetailsByOrders(@Param("orders") List<RoomRVOrder> orders);
} 