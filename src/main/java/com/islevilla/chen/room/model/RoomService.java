package com.islevilla.chen.room.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
		 try {
	        roomRepository.deleteById(roomId);
	    } catch (DataIntegrityViolationException e) {
	        throw new IllegalStateException("此房間尚有訂單資料，無法刪除");
	    }
	}
	
	public Room findById(Integer roomId) {
		Optional<Room> optional=roomRepository.findById(roomId);
		return optional.orElse(null);
	}
	
	public List<Room> findAll() {
		return roomRepository.findAll();
	}
	
	public List<Room> compoundQuery(Integer roomId, Integer roomTypeId, Byte roomStatus){
	    return roomRepository.searchRooms(roomId,roomTypeId, roomStatus);
	}
}
