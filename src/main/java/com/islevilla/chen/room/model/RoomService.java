package com.islevilla.chen.room.model;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
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
	
    
    //更新特定房型的基本統計
    @Transactional
    public RoomTypeCount updateRoomTypeBasicStatistics(Integer roomTypeId) {
        // 計算該房型的總房間數
        List<RoomTypeCount> totalRooms = roomRepository.countRoomsByRoomTypeId();
        Integer totalRoom = totalRooms.get(roomTypeId).getRoomCountDTO();
        
        // 計算無法使用的房間數 (狀態 2:待維修, 4:停用)
        List<RoomTypeCount> totalUnables = roomRepository.countRoomsByRoomTypeIdWithStatus();
        Integer totalUnable = totalUnables.get(roomTypeId).getRoomUnableDTO();
        
        // 基本可用數量 = 總數 - 無法使用
        Integer Available = totalRoom - totalUnable;
        
        // 建立統計結果
        RoomTypeCount statistics = new RoomTypeCount();
        statistics.setRoomTypeIdDTO(roomTypeId);
        statistics.setRoomCountDTO(totalRoom);
        statistics.setRoomUnableDTO(totalUnable);
        statistics.setRoomAvailableDTO(Available);
        
        return statistics;
    }
    
    //更新所有房型的基本統計
    @Transactional
    public void updateAllRoomTypeBasicStatistics() {
        List<RoomTypeCount> allRoomTypes = roomRepository.countRoomsByRoomTypeId();
        for (RoomTypeCount roomType : allRoomTypes) {
            updateRoomTypeBasicStatistics(roomType.getRoomTypeIdDTO());
        }
    }
}
