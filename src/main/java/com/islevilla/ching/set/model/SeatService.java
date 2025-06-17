package com.islevilla.ching.set.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeatService {

	@Autowired
	private SeatRepository seatRepository;
	
	public void addSeat(Seat seat) {
		seatRepository.save(seat);
	}
	
	public void updateSeat(Seat seat) {
		seatRepository.save(seat);
	}
	
	public void deleteSeat(Seat seat) {
		seatRepository.save(seat);
	}
	
	
	
	
	
	
	
	
}
