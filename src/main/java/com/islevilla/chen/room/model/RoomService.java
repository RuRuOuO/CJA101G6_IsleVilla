package com.islevilla.chen.room.model;

import java.util.ArrayList;
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
	
	// 根據房型ID查詢房間
	public List<Room> findByRoomTypeId(Integer roomTypeId) {
	    return roomRepository.findByRoomTypeId(roomTypeId);
	}

	// 根據房間狀態查詢房間
	public List<Room> findByRoomStatus(Byte roomStatus) {
	    return roomRepository.findByRoomStatus(roomStatus);
	}

	// 複合查詢方法（可選）
	public List<Room> findByMultipleConditions(Integer roomId, Integer roomTypeId, Byte roomStatus) {
	    if (roomId != null && roomId > 0) {
	        Room room = findById(roomId);
	        return room != null ? List.of(room) : new ArrayList<>();
	    }
	    
	    if (roomTypeId != null && roomTypeId > 0 && roomStatus != null) {
	        return roomRepository.findByRoomTypeIdAndRoomStatus(roomTypeId, roomStatus);
	    }
	    
	    if (roomTypeId != null && roomTypeId > 0) {
	        return findByRoomTypeId(roomTypeId);
	    }
	    
	    if (roomStatus != null) {
	        return findByRoomStatus(roomStatus);
	    }
	    return null;
	}
}
