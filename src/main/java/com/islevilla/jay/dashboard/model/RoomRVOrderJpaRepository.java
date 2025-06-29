package com.islevilla.jay.dashboard.model;

import com.islevilla.wei.room.model.RoomRVOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dashboard專用的房間訂單JPA Repository介面
 */
@Repository
public interface RoomRVOrderJpaRepository extends JpaRepository<RoomRVOrder, Integer> {
    
    /**
     * 統計指定時間範圍的房間訂單數量
     */
    long countByRoomOrderDateBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查詢指定時間範圍的房間訂單
     */
    List<RoomRVOrder> findByRoomOrderDateBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查詢指定時間範圍的房間訂單並按時間降序排列
     */
    List<RoomRVOrder> findByRoomOrderDateBetweenOrderByRoomOrderDateDesc(LocalDateTime startTime, LocalDateTime endTime);
} 