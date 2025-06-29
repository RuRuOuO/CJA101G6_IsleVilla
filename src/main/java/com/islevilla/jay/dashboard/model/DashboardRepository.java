package com.islevilla.jay.dashboard.model;

import com.islevilla.wei.room.model.RoomRVOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DashboardRepository {

    @Autowired
    private RoomRVOrderJpaRepository roomRVOrderJpaRepository;

    /**
     * 統計指定時間範圍內的房間訂單數量
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @return 房間訂單數量
     */
    public long countRoomOrdersByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return roomRVOrderJpaRepository.countByRoomOrderDateBetween(startTime, endTime);
    }

    /**
     * 查詢指定時間範圍內的房間訂單
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @return 房間訂單列表
     */
    public List<RoomRVOrder> findRoomOrdersByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return roomRVOrderJpaRepository.findByRoomOrderDateBetween(startTime, endTime);
    }

    /**
     * 查詢指定時間範圍內的房間訂單並按時間降序排列
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @return 房間訂單列表
     */
    public List<RoomRVOrder> findRoomOrdersByDateRangeOrderByDateDesc(LocalDateTime startTime, LocalDateTime endTime) {
        return roomRVOrderJpaRepository.findByRoomOrderDateBetweenOrderByRoomOrderDateDesc(startTime, endTime);
    }
} 