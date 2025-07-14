package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.room.model.RoomRVOrder;

@Repository
public interface ShuttleReservationRepository extends JpaRepository<ShuttleReservation, Integer> {

	// 根據會員查詢預約記錄（根據接駁預約編號Desc）
	List<ShuttleReservation> findByMembersOrderByShuttleReservationIdDesc(Members member);

	// 根據訂房ID查詢預約記錄
	List<ShuttleReservation> findByRoomRVOrder(RoomRVOrder roomRVOrder);

	// 查詢會員在特定日期、班次和方向的預約
	List<ShuttleReservation> findByMembersAndShuttleDateAndShuttleScheduleAndShuttleDirection(Members member,
			LocalDate shuttleDate, ShuttleSchedule shuttleSchedule, Integer shuttleDirection);

	// 根據訂房訂單、接駁方向和接駁預約狀態檢查是否存在
	boolean existsByRoomRVOrderAndShuttleDirectionAndShuttleReservationStatus(RoomRVOrder roomRVOrder,
			Integer shuttleDirection, Integer shuttleReservationStatus);

}
