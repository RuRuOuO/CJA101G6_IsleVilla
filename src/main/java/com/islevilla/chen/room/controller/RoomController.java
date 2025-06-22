package com.islevilla.chen.room.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.room.model.RoomService;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/backend/room")
public class RoomController {
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private RoomTypeService roomTypeService;
	
//	// 創建一個 Map 來存房間狀態對應的名稱
//	private static final Map<Integer, String> roomStatusMap;
//
//	static {
//	    roomStatusMap = new HashMap<>();
//	    roomStatusMap.put(0, "空房");
//	    roomStatusMap.put(1, "入住中");
//	    roomStatusMap.put(2, "待清潔");
//	    roomStatusMap.put(3, "待清潔");
//	}

	
// 顯示AddRoom網頁
@GetMapping("/addRoom")
public String showAddRoom(Model model) {
	
	Room room = new Room();
	
    // 創建房型名稱對應表（查詢結果需要顯示房型名稱）
    Map<Integer, String> roomTypeNameMap = new HashMap<>();
    List<RoomType> roomTypeList = roomTypeService.findAll();
    for (RoomType roomType : roomTypeList) {
        roomTypeNameMap.put(roomType.getRoomTypeId(), roomType.getRoomTypeName());
    }
    
 // 建立房間狀態對應表
    Map<Byte, String> roomStatusMap = new HashMap<>();
    roomStatusMap.put((byte)0, "空房");
    roomStatusMap.put((byte)1, "入住中");
    roomStatusMap.put((byte)2, "待維修");
    roomStatusMap.put((byte)3, "待清潔");
    roomStatusMap.put((byte)4, "停用");
    
	System.out.println("進入頁面");
	model.addAttribute("room", room); 
	model.addAttribute("roomTypeList", roomTypeList); 
	model.addAttribute("roomStatusMap", roomStatusMap); 
	return "/back-end/room/addRoom";
}
	
	//處理網頁送出的請求
	@PostMapping("/addRoom")
	public String addRoom(@ModelAttribute("room") Room room, Model model) {
		Room existingRoom = roomService.findById(room.getRoomId());
		 List<String> errorMessages = new ArrayList<>();
		
		//檢查房間是否已存在（避免重複）
		if(existingRoom!=null) {
			System.out.println("資料已存在");
			errorMessages.add("房間ID已存在");
		}
		//檢查房型ID是否存在（避免外鍵錯誤）
		if(roomTypeService.findById(room.getRoomTypeId()) == null) {
			errorMessages.add("房型ID不存在，請重新輸入");
		}

		if(!errorMessages.isEmpty()) {
			model.addAttribute("errorMessage", errorMessages);
			return "/back-end/room/addRoom";
		}else{
			roomService.addRoom(room);
			System.out.println("資料送出成功");
			model.addAttribute("successMessage", "房間新增成功！");
			return "/back-end/room/addRoom";
		}
		
	}

	
// 顯示SelectPage網頁
@GetMapping("/selectRoomPage")
public String showSelectPage(Model model) {
	Room room = new Room();
	
	// 獲取所有房型供下拉選單使用
    List<RoomType> roomTypeList = roomTypeService.findAll();
    
//    // 建立房間狀態對應表
    Map<Byte, String> roomStatusMap = new HashMap<>();
    roomStatusMap.put((byte)0, "空房");
    roomStatusMap.put((byte)1, "入住中");
    roomStatusMap.put((byte)2, "待維修");
    roomStatusMap.put((byte)3, "待清潔");
    roomStatusMap.put((byte)4, "停用");
    
	System.out.println("進入頁面");
    model.addAttribute("room", room);
    model.addAttribute("roomTypeList", roomTypeList);
    model.addAttribute("roomStatusMap", roomStatusMap);
	return "back-end/room/selectRoomPage";
}	
	
	@PostMapping("/selectRoomPage")
	public String searchRoom(@RequestParam(required = false) Integer roomId,
	                        @RequestParam(required = false) Integer roomTypeId,
	                        @RequestParam(required = false) Byte roomStatus,
	                        Model model) {
	    
	    List<Room> searchResult = new ArrayList<>();
	    List<String> errorMessage = new ArrayList<>();
	    System.out.println(roomId);
	        // 根據不同條件進行查詢
	        if (roomId != null && roomId > 0) {
	        	
	            // 按房間ID查詢
	            Room room = roomService.findById(roomId);
	            if (room != null) {
	            	System.out.println("查詢房間ID");
	                searchResult.add(room);
	            }
	        } else if (roomTypeId != null && roomTypeId > 0) {
	            // 按房型ID查詢
	        	System.out.println("查詢房型ID");
	            searchResult = roomService.findByRoomTypeId(roomTypeId);
	        } else if (roomStatus != null) {
	            // 按房間狀態查詢
	        	System.out.println("查詢房間狀態");
	            searchResult = roomService.findByRoomStatus(roomStatus);
	        }
	        
	        if (searchResult.isEmpty()) {
	            errorMessage.add("查無符合條件的房間資料");
	        }
	    
	    // 創建房型名稱對應表（查詢結果需要顯示房型名稱）
	    Map<Integer, String> roomTypeNameMap = new HashMap<>();
	    List<RoomType> roomTypeList = roomTypeService.findAll();
	    for (RoomType roomType : roomTypeList) {
	        roomTypeNameMap.put(roomType.getRoomTypeId(), roomType.getRoomTypeName());
	    }
	    
	    // 建立房間狀態對應表
	    Map<Byte, String> roomStatusMap = new HashMap<>();
	    roomStatusMap.put((byte)0, "空房");
	    roomStatusMap.put((byte)1, "入住中");
	    roomStatusMap.put((byte)2, "待維修");
	    roomStatusMap.put((byte)3, "待清潔");
	    roomStatusMap.put((byte)4, "停用");
	    
	    // 將結果傳遞給頁面
	    model.addAttribute("searchResult", searchResult);
	    model.addAttribute("roomTypeNameMap", roomTypeNameMap);
	    model.addAttribute("roomStatusMap", roomStatusMap);
	    
	    if (!errorMessage.isEmpty()) {
	        model.addAttribute("errorMessage", errorMessage);
	        return "back-end/room/selectRoomPage";
	    }
	    
	    System.out.println("查詢完成，找到 " + searchResult.size() + " 筆資料");
	    return "back-end/room/searchRoom";
	}

// 顯示SearchRoom網頁
	@GetMapping("/searchRoom")
	public String showSearchRoom() {
//		List<Room> searchResult = model.getAttribute("searchResult");
//		
		return "back-end/room/searchRoom";
		
	}

// 顯示ListAllRoom網頁
@GetMapping("/listAllRoom")
public String showListAllRoom(Model model) {
	
	List<Room> roomList = roomService.findAll();
	
	// 創建一個 Map 來存房型ID對應的房型名稱
	Map<Integer, String> roomTypeNameMap = new HashMap<>();
	
	// 獲取所有房型資料並建立對應關係
	for (Room room : roomList) {
		RoomType roomType = roomTypeService.findById(room.getRoomTypeId());
		if (roomType != null) {
			roomTypeNameMap.put(room.getRoomTypeId(), roomType.getRoomTypeName());
		}
	}
	// 建立房間狀態對應表
    Map<Byte, String> roomStatusMap = new HashMap<>();
    roomStatusMap.put((byte)0, "空房");
    roomStatusMap.put((byte)1, "入住中");
    roomStatusMap.put((byte)2, "待維修");
    roomStatusMap.put((byte)3, "待清潔");
    roomStatusMap.put((byte)4, "停用");

	System.out.println("進入頁面");
	model.addAttribute("roomList", roomList);  
	model.addAttribute("roomStatusMap", roomStatusMap);  
	model.addAttribute("roomTypeNameMap", roomTypeNameMap);  
	return "back-end/room/listAllRoom";
}	

//顯示UpdateRoom網頁
@GetMapping("/updateRoom/{roomId}")
public String showUpdateRoom(@PathVariable Integer roomId, Model model) {
    System.out.println("要修改的房間ID: " + roomId);
    
    // 根據 roomId 查詢房間資料
    Room room = roomService.findById(roomId);
    // 將房間資料傳給前端頁面
    model.addAttribute("room", room);
    return "back-end/room/updateRoom";
}

	@PostMapping("/updateRoom")
	public String updateRoom(@ModelAttribute("room") Room room, Model model) {
	    List<String> errorMessages = new ArrayList<>();
		
		//檢查房型ID是否存在（避免外鍵錯誤）
		if(roomTypeService.findById(room.getRoomTypeId()) == null) {
			errorMessages.add("房型ID不存在，請重新輸入");
		}
		if(!errorMessages.isEmpty()) {
			model.addAttribute("errorMessage", errorMessages);
			model.addAttribute("room", room);
			return "/back-end/room/updateRoom";
		}else{
			roomService.updateRoom(room);
			System.out.println("資料送出成功");
			model.addAttribute("successMessage", "房間資料更新成功！");
			model.addAttribute("room", room);
			return "/back-end/room/updateRoom";
		}
	}
}
