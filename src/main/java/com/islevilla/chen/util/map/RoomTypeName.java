package com.islevilla.chen.util.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.islevilla.chen.room.model.RoomService;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;

@Component
public class RoomTypeName {
	
	@Autowired
	private RoomTypeService roomTypeService;
	
	// 創建房型名稱對應表（查詢結果需要顯示房型名稱）
	public Map<Integer, String> getRoomTypeNameMap(){
	    Map<Integer, String> roomTypeNameMap = new HashMap<>();
	    // 顯示下拉選單選項
	    List<RoomType> roomTypeList = roomTypeService.findAll();
	    for (RoomType roomType : roomTypeList) {
	        roomTypeNameMap.put(roomType.getRoomTypeId(), roomType.getRoomTypeName());
	    }
	    return roomTypeNameMap;
	}
	
	// 房型下拉選單選項
	public List<RoomType> getRoomTypeNameList(){
	    List<RoomType> roomTypeList = roomTypeService.findAll();
	    return roomTypeList;
	}
}
