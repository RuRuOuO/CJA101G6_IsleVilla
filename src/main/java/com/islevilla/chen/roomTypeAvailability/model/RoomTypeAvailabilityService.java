package com.islevilla.chen.roomTypeAvailability.model;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.chen.roomType.model.RoomType;

@Service
public class RoomTypeAvailabilityService {
	
	@Autowired
	private RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;
	
	public RoomTypeAvailability addRoomTypeAvailability(RoomTypeAvailability roomTypeAvailability) {
		return roomTypeAvailabilityRepository.save(roomTypeAvailability);
	}
	
	public RoomTypeAvailability updateRoomTypeAvailability(RoomTypeAvailability roomTypeAvailability) {
		return roomTypeAvailabilityRepository.save(roomTypeAvailability);
	}
	
//	public void deleteRoomTypeAvailability(int roomTypeId,Date roomTypeAvailabilityDate) {
//		roomTypeAvailabilityRepository.deleteById(int roomTypeId,Date roomTypeAvailabilityDate);
//	}
	
//	public RoomTypeAvailability findById(int roomTypeId,Date roomTypeAvailabilityDate) {
//		Optional<RoomType> optional=roomTypeAvailabilityRepository.findById(int roomTypeId,Date roomTypeAvailabilityDate);
//		roomTypeAvailabilityRepository
//		return optional.orElse(null);
//	}
	
	public List<RoomTypeAvailability> findAll() {
		return roomTypeAvailabilityRepository.findAll();
	}
}
