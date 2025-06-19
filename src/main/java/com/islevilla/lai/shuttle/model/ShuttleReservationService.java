package com.islevilla.lai.shuttle.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShuttleReservationService {

	@Autowired
	private ShuttleReservationRepository shuttleReservationRepository;

	public List<ShuttleReservation> getAllReservations() {
		return shuttleReservationRepository.findAll();
	}

	public Optional<ShuttleReservation> getReservationById(Integer id) {
		return shuttleReservationRepository.findById(id);
	}

	public List<ShuttleReservation> getReservationsByMemberId(Integer memberId) {
		return shuttleReservationRepository.findByMemberId(memberId);
	}

	public List<ShuttleReservation> getReservationsByRoomReservationId(Integer roomReservationId) {
		return shuttleReservationRepository.findByRoomReservationId(roomReservationId);
	}

	public List<ShuttleReservation> getReservationsByDate(LocalDate date) {
		return shuttleReservationRepository.findByShuttleDate(date);
	}

	public List<ShuttleReservation> getReservationsByScheduleAndDate(Integer scheduleId, LocalDate date) {
		return shuttleReservationRepository.findByShuttleScheduleIdAndShuttleDate(scheduleId, date);
	}

	public List<ShuttleReservation> getReservationsByStatus(Integer status) {
		return shuttleReservationRepository.findByShuttleReservationStatus(status);
	}

	public Integer getTotalReservedSeats(Integer scheduleId, LocalDate date) {
		return shuttleReservationRepository.getTotalReservedSeats(scheduleId, date);
	}

	@Transactional
	public ShuttleReservation saveReservation(ShuttleReservation reservation) {
		return shuttleReservationRepository.save(reservation);
	}

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

	public void deleteReservation(Integer id) {
		shuttleReservationRepository.deleteById(id);
	}

	public boolean existsById(Integer id) {
		return shuttleReservationRepository.existsById(id);
	}

}
