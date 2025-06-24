package com.islevilla.chen.roomType.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.room.model.Room;

@Service
public class RoomTypeService {

	@Autowired
	private RoomTypeRepository roomTypeRepository;
	
	@Transactional
	public RoomType addRoomType(RoomType roomType) {
		return roomTypeRepository.save(roomType);
	}
	
	@Transactional
	public RoomType updateRoomType(RoomType roomType) {
		return roomTypeRepository.save(roomType);
	}
	
	@Transactional
	public void deleteRoomType(Integer roomTypeId) {
		roomTypeRepository.deleteById(roomTypeId);
	}
	
	@Transactional(readOnly = true)
	public RoomType findById(Integer roomTypeId) {
		Optional<RoomType> optional=roomTypeRepository.findById(roomTypeId);
		return optional.orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<RoomType> findAll() {
		return roomTypeRepository.findAll();
	}
}

