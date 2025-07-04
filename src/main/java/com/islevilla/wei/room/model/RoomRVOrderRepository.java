package com.islevilla.wei.room.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.islevilla.chen.room.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.islevilla.lai.members.model.Members;


public interface RoomRVOrderRepository extends JpaRepository<RoomRVOrder, Integer> {
    // 接駁預約：驗證會員和訂房資料(去程)
    @Query("SELECT rro FROM RoomRVOrder rro WHERE rro.roomReservationId = :roomReservationId " +
            "AND rro.members.memberId = :memberId AND rro.roomOrderStatus = 0 " +
            "AND :shuttleDate = DATE(rro.checkInDate)")
    Optional<RoomRVOrder> validateMemberAndRoomReservationOutward(
            @Param("memberId") Integer memberId,
            @Param("roomReservationId") Integer roomReservationId,
            @Param("shuttleDate") LocalDate shuttleDate);

    // 接駁預約：驗證會員和訂房資料(回程)
    @Query("SELECT rro FROM RoomRVOrder rro WHERE rro.roomReservationId = :roomReservationId " +
            "AND rro.members.memberId = :memberId AND rro.roomOrderStatus = 0 " +
            "AND :shuttleDate = DATE(rro.checkOutDate)")
    Optional<RoomRVOrder> validateMemberAndRoomReservationReturn(
            @Param("memberId") Integer memberId,
            @Param("roomReservationId") Integer roomReservationId,
            @Param("shuttleDate") LocalDate shuttleDate);

    Page<RoomRVOrder> findAll(Pageable pageable);

    RoomRVOrder findByroomReservationId(Integer roomReservationId);

    List<RoomRVOrder> findByRoomOrderStatus(Integer roomRVOrderStatus);

    List<RoomRVOrder> findByRoomOrderStatusIn(List<Integer> roomRVOrderStatusIn);

    List<RoomRVOrder> findByMembers(Members member);

    List<RoomRVOrder> findByMembersAndRoomOrderStatus(Members members, Integer status);
    
    List<RoomRVOrder> findByMembersAndCheckInDateGreaterThan(Members members, LocalDate checkInDate);
    
    List<RoomRVOrder> findByMembersAndCheckOutDateGreaterThan(Members members, LocalDate checkOutDate);

    List<RoomRVOrder> findAllByOrderByRoomOrderDateDesc();

    // 查詢條件：該會員的訂單中，實際退房時間在過去一個月內，且訂單狀態為已退房
    @Query("SELECT r FROM RoomRVOrder r WHERE " +
            "r.members.memberId = :memberId AND " +
            "r.roomOrderStatus = :status AND " +
            "r.actualCheckOutDate IS NOT NULL AND " +
            "r.actualCheckOutDate >= :oneMonthAgo " +
            "ORDER BY r.actualCheckOutDate DESC")
    List<RoomRVOrder> findEligibleOrdersForFeedback(
            @Param("memberId") Integer memberId,
            @Param("oneMonthAgo") LocalDateTime oneMonthAgo,
            @Param("status") Integer status
    );
    
// ==================== 給 RoomTypeAvailability 庫存計算相關查詢 ====================
    
    /*
     * 查詢特定日期有效的訂單 (該日期在入住和退房日期之間)
     * 訂單狀態：0=已確認, 1=已入住, 2=已退房 (排除已取消等狀態)
     */
    @Query("SELECT rro FROM RoomRVOrder rro WHERE " +
           "rro.checkInDate <= :targetDate AND rro.checkOutDate > :targetDate " +
           "AND rro.roomOrderStatus IN (0, 1, 2)")
    List<RoomRVOrder> findActiveOrdersOnDate(@Param("targetDate") LocalDate targetDate);
    
    //查詢特定日期範圍內有效的訂單
    @Query("SELECT rro FROM RoomRVOrder rro WHERE " +
           "NOT (rro.checkOutDate <= :startDate OR rro.checkInDate >= :endDate) " +
           "AND rro.roomOrderStatus IN (0, 1, 2)")
    List<RoomRVOrder> findActiveOrdersInDateRange(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    //查詢特定房型在特定日期的有效訂單數量
    @Query("SELECT COUNT(rrd) FROM RoomRVOrder rro " +
           "JOIN rro.roomRVDetails rrd " +
           "WHERE rro.checkInDate <= :targetDate AND rro.checkOutDate > :targetDate " +
           "AND rro.roomOrderStatus IN (0, 1, 2) " +
           "AND rrd.roomType.roomTypeId = :roomTypeId")
    Long countReservedRoomsOnDate(
        @Param("roomTypeId") Integer roomTypeId, 
        @Param("targetDate") LocalDate targetDate
    );
    

    //批量查詢多個房型在特定日期的預訂數量
    @Query("SELECT rrd.roomType.roomTypeId, COUNT(rrd) FROM RoomRVOrder rro " +
           "JOIN rro.roomRVDetails rrd " +
           "WHERE rro.checkInDate <= :targetDate AND rro.checkOutDate > :targetDate " +
           "AND rro.roomOrderStatus IN (0, 1, 2) " +
           "GROUP BY rrd.roomType.roomTypeId")
    List<Object[]> countReservedRoomsByTypeOnDate(@Param("targetDate") LocalDate targetDate);
 // ==================== 給 RoomTypeAvailability 庫存計算相關查詢 ====================
}