package com.islevilla.wei.room.model;

import java.time.LocalDate;
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
            "AND rro.members.memberId = :memberId AND rro.roomOrderStatus = 2 " +
            "AND :shuttleDate = DATE(rro.checkInDate)")
    Optional<RoomRVOrder> validateMemberAndRoomReservationOutward(
            @Param("memberId") Integer memberId,
            @Param("roomReservationId") Integer roomReservationId,
            @Param("shuttleDate") LocalDate shuttleDate);

    // 接駁預約：驗證會員和訂房資料(回程)
    @Query("SELECT rro FROM RoomRVOrder rro WHERE rro.roomReservationId = :roomReservationId " +
            "AND rro.members.memberId = :memberId AND rro.roomOrderStatus = 2 " +
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
}