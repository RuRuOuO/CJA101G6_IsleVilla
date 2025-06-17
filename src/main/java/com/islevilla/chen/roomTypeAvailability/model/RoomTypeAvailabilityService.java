package com.islevilla.chen.roomTypeAvailability.model;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.chen.roomType.model.RoomTypeVO;

public class RoomTypeAvailabilityService {
	
	@Autowired
	private RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;
	
	public RoomTypeAvailabilityVO addRoom(RoomTypeAvailabilityVO roomTypeAvailabilityVO) {
		return roomTypeAvailabilityRepository.save(roomTypeAvailabilityVO);
	}
	
	public RoomTypeAvailabilityVO updateRoom(RoomTypeAvailabilityVO roomTypeAvailabilityVO) {
		return roomTypeAvailabilityRepository.save(roomTypeAvailabilityVO);
	}
	
//	public void deleteRoom(int roomTypeId,Date roomTypeAvailabilityDate) {
//		roomTypeAvailabilityRepository.deleteById(int roomTypeId,Date roomTypeAvailabilityDate);
//	}
	
//	public RoomTypeAvailabilityVO findById(int roomTypeId,Date roomTypeAvailabilityDate) {
//		Optional<RoomTypeVO> optional=roomTypeAvailabilityRepository.findById(int roomTypeId,Date roomTypeAvailabilityDate);
//		roomTypeAvailabilityRepository
//		return optional.orElse(null);
//	}
	
	public List<RoomTypeAvailabilityVO> findAll() {
		return roomTypeAvailabilityRepository.findAll();
	}
}
