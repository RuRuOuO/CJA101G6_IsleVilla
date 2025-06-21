package com.islevilla.chen.room.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

	@Autowired
	private RoomRepository roomRepository;
	
	public Room addRoom(Room room) {
		return roomRepository.save(room);
	}
	
	public Room updateRoom(Room room) {
		return roomRepository.save(room);
	}
	
	public void deleteRoom(Integer roomId) {
		roomRepository.deleteById(roomId);
	}
	
	public Room findById(Integer roomId) {
		Optional<Room> optional=roomRepository.findById(roomId);
		return optional.orElse(null);
	}
	
	public List<Room> findAll() {
		return roomRepository.findAll();
	}
}
