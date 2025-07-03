package com.islevilla.patty.booking.model;

import com.islevilla.wei.room.model.RoomRVDetail;
import com.islevilla.wei.room.model.RoomRVOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingDetailRepository extends JpaRepository<RoomRVDetail, Integer> {

    /**
     * 根據訂單列表，找出所有的訂單明細
     */
    @Query("SELECT d FROM RoomRVDetail d WHERE d.roomRVOrder IN :orders")
    List<RoomRVDetail> findDetailsByOrders(@Param("orders") List<RoomRVOrder> orders);
    
    
} 