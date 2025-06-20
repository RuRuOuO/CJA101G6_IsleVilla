package com.islevilla.chen.room.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.room.model.RoomService;
import com.islevilla.chen.roomType.model.RoomTypeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/backend/room")
public class RoomController {
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private RoomTypeService roomTypeService;
	
// 顯示網頁
@GetMapping("/addRoom")
public String showAddRoom(Model model) {
	Room room = new Room();
	System.out.println("進入頁面");
	model.addAttribute("room", room); 
	return "/back-end/room/addRoom";
}
	
	//處理網頁送出的請求
	@PostMapping("/addRoom")
	public String addRoom(@ModelAttribute("room") Room room, Model model) {
//		System.out.println("roomId: " + room.getRoomId());
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
//	//處理網頁送出的請求 其他方法 待研究
//	@PostMapping("/addRoom/submit")
//	public String addRoom(
//			@RequestParam("roomId") int roomId,
//			@RequestParam("roomTypeId") int roomTypeId,
//			@RequestParam("roomStatus") int roomStatus, Model model) {
//		roomService.insert(room);
//		return "/back-end/room/addRoom";
//	}
	
// 顯示網頁
@GetMapping("/selectRoomPage")
public String showSelectPage(Model model) {
	Room room = new Room();
	System.out.println("進入頁面");
	model.addAttribute("room", room); 
	return "back-end/room/selectRoomPage";
}	
	
//	//處理網頁送出的請求
//	@PostMapping("/selectRoomPage")
//	public String selectPage(@ModelAttribute("room") Room room, Model model) {
//		room = roomService.findById(room.getRoomId());
//		 List<String> errorMessages = new ArrayList<>();
//		
//		//檢查房間是否已存在（避免重複）
//		if(roomService.findById(room.getRoomId()) ==null) {
//			System.out.println("送出查詢");
//			errorMessages.add("查不到房間資料");
//		}
//		//檢查房型ID是否存在（避免外鍵錯誤）
//		if(roomTypeService.findById(room.getRoomTypeId()) == null) {
//			errorMessages.add("房型ID不存在，請重新輸入");
//		}
//
//		if(!errorMessages.isEmpty()) {
//			model.addAttribute("errorMessage", errorMessages);
//			return "/back-end/room/addRoom";
//		}else{
//			roomService.addRoom(room);
//			System.out.println("資料送出成功");
//			model.addAttribute("successMessage", "房間新增成功！");
//			return "/back-end/room/addRoom";
//		}
//		model.addAttribute("RoomId", roomId);
//		System.out.println("資料送出成功");
//		return "/back-end/room/findRoom";
//	}
// 顯示網頁
//@GetMapping("/listAllRoom")
//public String showListAllRoom(Model model) {
//	List<Room> list = new List<Room>;
//	System.out.println("進入頁面");
//	model.addAttribute("list", list); 
//	return "back-end/room/listAllRoom";
//}	
}
