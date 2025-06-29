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

	// 獲取所有預約記錄
	public List<ShuttleReservation> getAllReservations() {
		return shuttleReservationRepository.findAll();
	}

	// 根據ID獲取預約記錄
	public Optional<ShuttleReservation> getReservationById(Integer id) {
		return shuttleReservationRepository.findById(id);
	}

//	public List<ShuttleReservation> getReservationsByMemberId(Members member) {
//		return shuttleReservationRepository.findByMembers(member);
//	}

	// 根據會員獲取預約記錄
	public List<ShuttleReservation> getReservationsByMember(Members member) {
		return shuttleReservationRepository.findByMembers(member);
	}

	// 根據訂房記錄獲取預約記錄
	public List<ShuttleReservation> getReservationsByRoomReservation(RoomRVOrder roomRVOrder) {
		return shuttleReservationRepository.findByRoomRVOrder(roomRVOrder);
	}

	// 根據日期獲取預約記錄
	public List<ShuttleReservation> getReservationsByDate(LocalDate date) {
		return shuttleReservationRepository.findByShuttleDate(date);
	}

//	public List<ShuttleReservation> getReservationsByScheduleAndDate(Integer scheduleId, LocalDate date) {
//		return shuttleReservationRepository.findByShuttleScheduleIdAndShuttleDate(scheduleId, date);
//	}

	// 根據班次和日期獲取預約記錄
	public List<ShuttleReservation> getReservationsByScheduleAndDate(ShuttleSchedule shuttleSchedule, LocalDate date) {
		return shuttleReservationRepository.findByShuttleScheduleAndShuttleDate(shuttleSchedule, date);
	}

	// 根據班次、日期和方向獲取預約記錄
	public List<ShuttleReservation> getReservationsByScheduleDateAndDirection(ShuttleSchedule shuttleSchedule,
			LocalDate date, Integer direction) {
		return shuttleReservationRepository.findByShuttleScheduleAndShuttleDateAndShuttleDirection(shuttleSchedule,
				date, direction);
	}

	// 根據預約狀態獲取預約記錄
	public List<ShuttleReservation> getReservationsByStatus(Integer status) {
		return shuttleReservationRepository.findByShuttleReservationStatus(status);
	}

	// 獲取特定班次和日期的總預約座位數
	public Integer getTotalReservedSeats(Integer scheduleId, LocalDate date) {
		return shuttleReservationRepository.getTotalReservedSeats(scheduleId, date);
	}

	// 獲取特定班次、日期和方向的總預約座位數
	public Integer getTotalReservedSeatsByDirection(Integer scheduleId, LocalDate date, Integer direction) {
		return shuttleReservationRepository.getTotalReservedSeatsByDirection(scheduleId, date, direction);
	}

	// 統計特定班次和日期的預約數量
	public Long countReservationsByDateAndSchedule(LocalDate shuttleDate, ShuttleSchedule shuttleSchedule) {
		return shuttleReservationRepository.countReservationsByDateAndSchedule(shuttleDate, shuttleSchedule);
	}

	// 獲取會員在特定日期的有效預約
	public List<ShuttleReservation> getMemberReservationsByDate(Members member, LocalDate shuttleDate) {
		return shuttleReservationRepository.findByMembersAndShuttleDateAndShuttleReservationStatus(member, shuttleDate,
				1); // 1表示正常狀態
	}

	// 獲取會員在特定日期、班次和方向的預約
	public List<ShuttleReservation> getMemberReservationsBySchedule(Members member, LocalDate shuttleDate,
			ShuttleSchedule shuttleSchedule, Integer direction) {
		return shuttleReservationRepository.findByMembersAndShuttleDateAndShuttleScheduleAndShuttleDirection(member,
				shuttleDate, shuttleSchedule, direction);
	}

	// 根據訂房編號獲取有效預約記錄
	public List<ShuttleReservation> getValidReservationsByRoomOrder(RoomRVOrder roomRVOrder) {
		return shuttleReservationRepository.findByRoomRVOrderAndShuttleReservationStatus(roomRVOrder, 1);
	}

	// 獲取日期範圍內的預約記錄
	public List<ShuttleReservation> getReservationsByDateRange(LocalDate startDate, LocalDate endDate) {
		return shuttleReservationRepository.findByShuttleDateBetween(startDate, endDate);
	}

	// 獲取日期範圍內指定狀態的預約記錄
	public List<ShuttleReservation> getReservationsByDateRangeAndStatus(LocalDate startDate, LocalDate endDate,
			Integer status) {
		return shuttleReservationRepository.findByShuttleDateBetweenAndShuttleReservationStatus(startDate, endDate,
				status);
	}

	// 保存預約記錄
	@Transactional
	public ShuttleReservation saveReservation(ShuttleReservation reservation) {
		return shuttleReservationRepository.save(reservation);
	}

	// 更新預約記錄
	@Transactional
	public ShuttleReservation updateReservation(ShuttleReservation reservation) {
		if (!shuttleReservationRepository.existsById(reservation.getShuttleReservationId())) {
			throw new RuntimeException("預約記錄不存在");
		}
		return shuttleReservationRepository.save(reservation);
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

	// 刪除預約記錄
	@Transactional
	public void deleteReservation(Integer id) {
		if (!shuttleReservationRepository.existsById(id)) {
			throw new RuntimeException("預約記錄不存在");
		}
		shuttleReservationRepository.deleteById(id);
	}

	// 檢查預約記錄是否存在
	public boolean existsById(Integer id) {
		return shuttleReservationRepository.existsById(id);
	}

	// 檢查會員是否已經預約特定班次
	public boolean hasMemberReserved(Members member, LocalDate shuttleDate, 
			ShuttleSchedule shuttleSchedule, Integer direction) {
		List<ShuttleReservation> reservations = getMemberReservationsBySchedule(
				member, shuttleDate, shuttleSchedule, direction);
		return !reservations.isEmpty();
	}
	
	
	
}
