package com.islevilla.chen.roomType.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.islevilla.chen.room.model.Room;

@Service
public class RoomTypeService {

	@Autowired
	private RoomTypeRepository roomTypeRepository;
	
	public RoomType addRoomType(RoomType roomType) {
		return roomTypeRepository.save(roomType);
	}
	
	public RoomType updateRoomType(RoomType roomType) {
		return roomTypeRepository.save(roomType);
	}
	
	public void deleteRoomType(int roomTypeId) {
		roomTypeRepository.deleteById(roomTypeId);
	}
	
	public RoomType findById(int roomTypeId) {
		Optional<RoomType> optional=roomTypeRepository.findById(roomTypeId);
		return optional.orElse(null);
	}
	
	public List<RoomType> findAll() {
		return roomTypeRepository.findAll();
	}
}

