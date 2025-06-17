package com.islevilla.chen.roomType.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.islevilla.chen.room.model.RoomVO;

@Service
public class RoomTypeService {

	@Autowired
	private RoomTypeRepository roomTypeRepository;
	
	public RoomTypeVO addRoom(RoomTypeVO roomTypeVO) {
		return roomTypeRepository.save(roomTypeVO);
	}
	
	public RoomTypeVO updateRoom(RoomTypeVO roomTypeVO) {
		return roomTypeRepository.save(roomTypeVO);
	}
	
	public void deleteRoom(int roomTypeId) {
		roomTypeRepository.deleteById(roomTypeId);
	}
	
	public RoomTypeVO findById(int roomTypeId) {
		Optional<RoomTypeVO> optional=roomTypeRepository.findById(roomTypeId);
		return optional.orElse(null);
	}
	
	public List<RoomTypeVO> findAll() {
		return roomTypeRepository.findAll();
	}
}

