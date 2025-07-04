package com.islevilla.chen.room.model;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.util.exception.BusinessException;

@Service
public class RoomService {

	@Autowired
	private RoomRepository roomRepository;
	
	@Transactional
	public Room addRoom(Room room) {
		if(roomRepository.findById(room.getRoomId()).isPresent()) {
	        throw new BusinessException("房間ID已存在！");
	    }
		return roomRepository.save(room);
	}
	
	@Transactional
	public Room updateRoom(Room room) {
		return roomRepository.save(room);
	}
	
	@Transactional
	public void deleteRoom(Integer roomId) {
	        roomRepository.deleteById(roomId);
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
