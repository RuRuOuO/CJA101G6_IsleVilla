package com.islevilla.chen.room.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

	@Autowired
	private RoomRepository roomRepository;
	
	public RoomVO addRoom(RoomVO roomVO) {
		return roomRepository.save(roomVO);
	}
	
	public RoomVO updateRoom(RoomVO roomVO) {
		return roomRepository.save(roomVO);
	}
	
	public void deleteRoom(int roomId) {
		roomRepository.deleteById(roomId);
	}
	
	public RoomVO findById(int roomId) {
		Optional<RoomVO> optional=roomRepository.findById(roomId);
		return optional.orElse(null);
	}
	
	public List<RoomVO> findAll() {
		return roomRepository.findAll();
	}
}
