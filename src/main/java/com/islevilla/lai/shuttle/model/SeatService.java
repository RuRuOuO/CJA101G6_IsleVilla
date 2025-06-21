package com.islevilla.lai.shuttle.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeatService {

	@Autowired
	private SeatRepository seatRepository;

	public List<Seat> getAllSeats() {
		return seatRepository.findAll();
	}

	public Optional<Seat> getSeatById(Integer id) {
		return seatRepository.findById(id);
	}

	public Seat getSeatBySeatNumber(String seatNumber) {
		return seatRepository.findBySeatNumber(seatNumber);
	}

	public List<Seat> getAvailableSeats() {
		return seatRepository.findBySeatStatus(1);
	}

	public List<Seat> getSeatsByStatus(Integer status) {
		return seatRepository.findBySeatStatus(status);
	}

	public long getAvailableSeatsCount() {
		return seatRepository.countAvailableSeats();
	}

	public Seat saveSeat(Seat seat) {
		return seatRepository.save(seat);
	}

	public void deleteSeat(Integer id) {
		seatRepository.deleteById(id);
	}

	public boolean existsById(Integer id) {
		return seatRepository.existsById(id);
	}

}
