package com.islevilla.jay.dashboard.model;

import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersRepository;
import com.islevilla.lai.shuttle.model.ShuttleReservation;
import com.islevilla.lai.shuttle.model.ShuttleReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import com.islevilla.jay.dashboard.model.DashboardMembersRepository;
import com.islevilla.jay.dashboard.model.DashboardShuttleReservationRepository;

@Repository
public class DashboardRepository {

    @Autowired
    private RoomRVOrderJpaRepository roomRVOrderJpaRepository;

    @Autowired
    private DashboardMembersRepository membersRepository;

    @Autowired
    private DashboardShuttleReservationRepository shuttleReservationRepository;

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

    // 查詢今日新增會員數
    public long countTodayNewMembers(LocalDateTime start, LocalDateTime end) {
        return membersRepository.countByMemberCreatedAtBetween(start, end);
    }

    // 查詢今日搭乘接駁車人數（狀態=1）
    public long countTodayShuttlePassengers(LocalDate today) {
        // 只統計狀態為1的預約
        return shuttleReservationRepository.sumShuttleNumberByShuttleDateAndStatus(today, 1);
    }
} 