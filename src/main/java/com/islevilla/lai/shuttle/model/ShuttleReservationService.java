package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.room.model.RoomRVOrder;

@Service
public class ShuttleReservationService {

	@Autowired
	private ShuttleReservationRepository shuttleReservationRepository;
	
	// 根據會員獲取預約記錄（根據接駁預約編號Desc）
	public List<ShuttleReservation> getReservationsByMemberOrderByShuttleReservationIdDesc(Members member) {
		return shuttleReservationRepository.findByMembersOrderByShuttleReservationIdDesc(member);
	}

	// 根據訂房記錄獲取預約記錄
	public List<ShuttleReservation> getReservationsByRoomReservation(RoomRVOrder roomRVOrder) {
		return shuttleReservationRepository.findByRoomRVOrder(roomRVOrder);
	}

	// 獲取會員在特定日期、班次和方向的預約
	public List<ShuttleReservation> getMemberReservationsBySchedule(Members member, LocalDate shuttleDate,
			ShuttleSchedule shuttleSchedule, Integer direction) {
		return shuttleReservationRepository.findByMembersAndShuttleDateAndShuttleScheduleAndShuttleDirection(member,
				shuttleDate, shuttleSchedule, direction);
	}

	// 取消預約記錄
	@Transactional
	public ShuttleReservation cancelReservation(Integer id) {
		Optional<ShuttleReservation> reservationOpt = shuttleReservationRepository.findById(id);
		if (reservationOpt.isPresent()) {
			ShuttleReservation reservation = reservationOpt.get();
			reservation.setShuttleReservationStatus(0); // 設為取消狀態
			return shuttleReservationRepository.save(reservation);
		}
		throw new RuntimeException("預約記錄不存在");
	}

	// 根據訂房編號檢查是否已經預約去程接駁
	public boolean existsByRoomRVOrderAndShuttleReservationStatusDeparture(RoomRVOrder roomRVOrder) {
		return shuttleReservationRepository
				.existsByRoomRVOrderAndShuttleDirectionAndShuttleReservationStatus(roomRVOrder, 0, 1);
	}

	// 根據訂房編號檢查是否已經預約回程接駁
	public boolean existsByRoomRVOrderAndShuttleReservationStatusArrival(RoomRVOrder roomRVOrder) {
		return shuttleReservationRepository
				.existsByRoomRVOrderAndShuttleDirectionAndShuttleReservationStatus(roomRVOrder, 1, 1);
	}

	// 檢查會員是否已經預約特定班次
	public boolean hasMemberReserved(Members member, LocalDate shuttleDate, ShuttleSchedule shuttleSchedule,
			Integer direction) {
		List<ShuttleReservation> reservations = getMemberReservationsBySchedule(member, shuttleDate, shuttleSchedule,
				direction);
		return !reservations.isEmpty();
	}

}
