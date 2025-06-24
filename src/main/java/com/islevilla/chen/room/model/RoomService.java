package com.islevilla.chen.room.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

	@Autowired
	private RoomRepository roomRepository;
	
	@Transactional
	public Room addRoom(Room room) {
		return roomRepository.save(room);
	}
	
	@Transactional
	public Room updateRoom(Room room) {
		return roomRepository.save(room);
	}
	
	@Transactional
	public void deleteRoom(Integer roomId) {
		 try {
	        roomRepository.deleteById(roomId);
	    } catch (DataIntegrityViolationException e) {
	        throw new IllegalStateException("此房間尚有訂單資料，無法刪除");
	    }
	}
	@Transactional(readOnly = true)
	public Room findById(Integer roomId) {
		Optional<Room> optional=roomRepository.findById(roomId);
		return optional.orElse(null);
	}
	@Transactional(readOnly = true)
	public List<Room> findAll() {
		return roomRepository.findAll();
	}
	@Transactional(readOnly = true)
	public List<Room> compoundQuery(Integer roomId, Integer roomTypeId, Byte roomStatus){
	    return roomRepository.searchRooms(roomId,roomTypeId, roomStatus);
	}
}
