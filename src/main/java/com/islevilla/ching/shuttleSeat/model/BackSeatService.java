package com.islevilla.ching.shuttleSeat.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BackSeatService {
	
	@Autowired
	private BackSeatRepository backSeatRepository;
	
	//新增座位
	public void addSeat(BackSeat backSeat) {
		backSeatRepository.save(backSeat);
	}
	
	//修改座位
	public void updateSeat(BackSeat backSeat) {
		backSeatRepository.save(backSeat);
	}
	
	//刪除座位
	public void deleteSeat(Integer seatId) {
		if(backSeatRepository.existsById(seatId)) {
			backSeatRepository.deleteById(seatId);
		}
	}
	
	//查詢所有座位
	public List<BackSeat> getAllSeat(){
		return backSeatRepository.findAll();
	}
	
	//從seatId查詢單筆座位
	public BackSeat getSeatById(Integer seatId) {
		Optional<BackSeat> optional = backSeatRepository.findById(seatId);
		return optional.orElse(null);
	}
	
//	public List<BackSeatDTO> getAllSeat
}
