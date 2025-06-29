package com.islevilla.ching.shuttleReservationSeat.model;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BackShuttleReservationSeatService {

	@Autowired
	private BackShuttleReservationSeatRepository backShuttleReservationSeatRepository;

	// 單筆查詢(用複合式主鍵)
	public BackShuttleReservationSeat getBackShuttleReservationSeatById(Integer shuttleReservationId, Integer seatId) {

		BackShuttleReservationSeat.ShuttleReservationSeatId id = new BackShuttleReservationSeat.ShuttleReservationSeatId(
				shuttleReservationId, seatId);

		Optional<BackShuttleReservationSeat> optional = backShuttleReservationSeatRepository.findById(id);
		return optional.orElse(null);
	}

	// 查詢某個預約編號下的所有座位
	public List<BackShuttleReservationSeat> getByBackShuttleReservationId(Integer shuttleReservationId) {
		return backShuttleReservationSeatRepository.findByShuttleReservationId(shuttleReservationId);
	}

	// 查詢某個座位號碼下的所有座位
	public List<BackShuttleReservationSeat> getBySeatNumber(String seatNumber) {
		return backShuttleReservationSeatRepository.findBySeat_SeatNumber(seatNumber);
	}

	// 查詢某個座位編號下的所有座位
	public List<BackShuttleReservationSeat> getBySeatId(Integer seatId) {
		return backShuttleReservationSeatRepository.findBySeatId(seatId);
	}

	// 查詢全部
	public List<BackShuttleReservationSeat> getAllShuttleReservationSeat() {
		return backShuttleReservationSeatRepository.findAll();
	}

	// 預約編號排序 小>大
	public List<BackShuttleReservationSeat> getAllShuttleReservationSeatDesc() {
		List<BackShuttleReservationSeat> list = backShuttleReservationSeatRepository.findAll();
		list.sort(Comparator.comparing(BackShuttleReservationSeat::getShuttleReservationId));
		return list;
	}
}
