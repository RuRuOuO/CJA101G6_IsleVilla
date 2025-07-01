package com.islevilla.chen.roomType.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.chen.util.map.RoomTypeName;

@Service
public class RoomTypeService {

	@Autowired
	private RoomTypeRepository roomTypeRepository;
	
	@Transactional
	public RoomType addRoomType(RoomType roomType) {
		
		// 檢查資料庫中是否已存在相同的代碼
	    boolean existsByRoomTypeCode = roomTypeRepository.existsByRoomTypeCode(roomType.getRoomTypeCode());
	    if (existsByRoomTypeCode) {
	        throw new BusinessException("房型代碼已存在: " + roomType.getRoomTypeCode());
	    }
	    // 檢查資料庫中是否已存在相同的名稱
	    boolean existsByRoomTypeName = roomTypeRepository.existsByRoomTypeName(roomType.getRoomTypeName());
	    if (existsByRoomTypeName) {
	    	throw new BusinessException("房型名稱已存在: " + roomType.getRoomTypeName());
	    }
	    
		return roomTypeRepository.save(roomType);
	}
	
	@Transactional
	public RoomType updateRoomType(RoomType roomType) {

		// 檢查房型代碼是否與其他房型重複（排除自己）
		Optional<RoomType> roomTypeWithSameCode = roomTypeRepository.findByRoomTypeCode(roomType.getRoomTypeCode());
		//但查到的 ID 和目前要更新的房型 ID 不同，代表代碼跟其他房型重複 ➔ 拋出例外阻止更新。
		if (roomTypeWithSameCode.isPresent() && 
			!roomTypeWithSameCode.get().getRoomTypeId().equals(roomType.getRoomTypeId())) {
			throw new BusinessException("房型代碼已存在: " + roomType.getRoomTypeCode());
		}
		
		// 檢查房型名稱是否與其他房型重複（排除自己）
		Optional<RoomType> roomTypeWithSameName = roomTypeRepository.findByRoomTypeName(roomType.getRoomTypeName());
		//但查到的 ID 和目前要更新的房型 ID 不同，代表代碼跟其他房型重複 ➔ 拋出例外阻止更新。
		if (roomTypeWithSameName.isPresent() && 
			!roomTypeWithSameName.get().getRoomTypeId().equals(roomType.getRoomTypeId())) {
			throw new BusinessException("房型名稱已存在: " + roomType.getRoomTypeName());
		}
		
		return roomTypeRepository.save(roomType);
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
	
	//複合查詢
	@Transactional(readOnly = true)
	public List<RoomType> compoundQuery(Integer roomTypeId, Byte roomTypeSaleStatus){
		return roomTypeRepository.searchRoomTypes(roomTypeId, roomTypeSaleStatus);
	}
	//根據房型上下架狀態查詢
	@Transactional(readOnly = true)
	public List<RoomType> findByRoomTypeSaleStatus(Byte roomTypeSaleStatus) {
		List<RoomType> list = roomTypeRepository.findByRoomTypeSaleStatus(roomTypeSaleStatus);
		 if (list.isEmpty()) {
		        throw new BusinessException("查無符合條件的房型資料！");
		    }
		return list;
	}
}

