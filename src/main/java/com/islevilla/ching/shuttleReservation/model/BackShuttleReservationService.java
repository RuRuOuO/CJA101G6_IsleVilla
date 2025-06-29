package com.islevilla.ching.shuttleReservation.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BackShuttleReservationService {

	@Autowired
	private BackShuttleReservationRepository backShuttleReservationRepository;

	    // 單筆查詢
		public BackShuttleReservation getShuttleReservationById(Integer shuttleReservationId) {
			Optional<BackShuttleReservation> optional = backShuttleReservationRepository.findById(shuttleReservationId);
			return optional.orElse(null);
		}

		// 全部查詢
		public List<BackShuttleReservation> getAllShuttleReservation() {
			return backShuttleReservationRepository.findAll();
		}
		
	// 新增
//	public void addShuttleReservation(ShuttleReservation shuttleReservation) {
//		shuttleReservationRepository.save(shuttleReservation);
//	}

	// 修改
//	public void updateShuttleReservation(ShuttleReservation shuttleReservation) {
//		shuttleReservationRepository.save(shuttleReservation);
//	}

	// 刪除
//	public void deleteShuttleReservation(Integer shuttleReservationId) {
//		if (shuttleReservationRepository.existsById(shuttleReservationId)) {
//			shuttleReservationRepository.deleteById(shuttleReservationId);
//		}
//	}

}