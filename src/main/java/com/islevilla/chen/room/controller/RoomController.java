package com.islevilla.chen.room.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.directory.SearchResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.room.model.RoomService;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityService;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.chen.util.map.RoomTypeName;
import com.islevilla.wei.PageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/backend/room")
public class RoomController {
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private RoomTypeService roomTypeService;
	
	@Autowired
	private RoomTypeAvailabilityService roomTypeAvailabilityService;
	
	@Autowired
	private RoomTypeName roomTypeName;
	
	// 創建一個 Map 來存房間狀態對應的名稱
	private static final Map<Byte, String> roomStatusMap;

	static {
	    roomStatusMap = new HashMap<>();
	    roomStatusMap.put((byte)0, "空房");
	    roomStatusMap.put((byte)1, "入住中");
	    roomStatusMap.put((byte)2, "待維修");
	    roomStatusMap.put((byte)3, "待清潔");
	    roomStatusMap.put((byte)4, "停用");
	}

	
// 顯示AddRoom網頁
@GetMapping("/addRoom")
public String showAddRoom(Model model) {
	
	Room room = new Room();
    List<RoomType> roomTypeList=roomTypeName.getRoomTypeNameList();
    
	System.out.println("進入頁面");
	model.addAttribute("room", room); 
	model.addAttribute("roomTypeList", roomTypeList); 
	model.addAttribute("roomStatusMap", roomStatusMap); 
	return "/back-end/room/addRoom";
}
	
//處理網頁送出的請求
	@PostMapping("/addRoom")
	public String addRoom(@Valid @ModelAttribute("room") Room room, BindingResult result, Model model) {
		// 房型下拉選單選項
	    List<RoomType> roomTypeList=roomTypeName.getRoomTypeNameList();
	    
		model.addAttribute("roomTypeList", roomTypeList); 
		model.addAttribute("roomStatusMap", roomStatusMap); 
		
		// 後端驗證
		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors().stream() 			 //取得錯誤資料集合
												.map(FieldError::getDefaultMessage)  //把每個錯誤轉成訊息
										        .collect(Collectors.toList());		 //收集成一個清單
			model.addAttribute("errorMessage", errorMessages);
			return "back-end/room/addRoom";
		}
		
		try {
	        roomService.addRoom(room);
			//增加新房間後，更新該房型的統計資料
	        roomService.updateRoomTypeBasicStatistics(room.getRoomTypeId());
	        // 更新該房型的統計資料後改寫roomTypeAvailability資料庫
	        roomTypeAvailabilityService.recalculateAvailability(roomService.updateRoomTypeBasicStatistics(room.getRoomTypeId()));
	        
			System.out.println("資料送出成功");
			model.addAttribute("successMessage", "房間新增成功！");
			model.addAttribute("room", room); 
			return "back-end/room/addRoom";
	    } catch (BusinessException e) {
	        // 統一處理例外，顯示給使用者
	        List<String> errorMessages = new ArrayList<>();
	        errorMessages.add(e.getMessage());
	        model.addAttribute("errorMessage", errorMessages);
	        return "/back-end/room/addRoom";
	    }
	}


	
// 顯示SelectPage網頁
@GetMapping("/selectRoomPage")
public String showSelectPage(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model,
                             HttpServletRequest request) {
	Room room = new Room();
	
	// 房型下拉選單選項
    List<RoomType> roomTypeList=roomTypeName.getRoomTypeNameList();
    
    // 分頁查詢
    Pageable pageable = PageRequest.of(page, size, Sort.by("roomId").ascending());
    Page<Room> roomPage = roomService.findAll(pageable);
    
    // 使用 PageUtil 將分頁資料加入 model
    PageUtil.ModelWithPage(roomPage, model, page, "roomList", request);
    
    // 創建房型名稱對應表
    Map<Integer, String> roomTypeNameMap=roomTypeName.getRoomTypeNameMap();
    
	System.out.println("進入頁面");
    model.addAttribute("room", room);
    model.addAttribute("roomTypeList", roomTypeList);
    model.addAttribute("roomStatusMap", roomStatusMap);
    
    // 將房型名稱對應表加到 model
    model.addAttribute("roomTypeNameMap", roomTypeNameMap);
    
	return "/back-end/room/selectRoomPage";
}	
	
	@PostMapping("/selectRoomPage")
	public String selectRoomPage(@RequestParam(required = false) Integer roomId,
	                        @RequestParam(required = false) Integer roomTypeId,
	                        @RequestParam(required = false) Byte roomStatus,
	                        Model model) {
	    List<String> errorMessage = new ArrayList<>();
	    
	    // 取得所有符合條件的資料（不分頁）
	    List<Room> fullResult = roomService.compoundQuery(roomId, roomTypeId, roomStatus);

	    if (fullResult.isEmpty()) {
	        errorMessage.add("查無符合條件的房間資料");
	    }

	    // 直接傳遞所有查詢結果，不進行分頁
	    model.addAttribute("searchResult", fullResult);

	    // 房型下拉選單選項
	    List<RoomType> roomTypeList=roomTypeName.getRoomTypeNameList();
	    // 創建房型名稱對應表（查詢結果需要顯示房型名稱）
	    Map<Integer, String> roomTypeNameMap=roomTypeName.getRoomTypeNameMap();
	    
	    // 將結果傳遞給頁面
	    model.addAttribute("roomTypeList", roomTypeList);
	    model.addAttribute("roomTypeNameMap", roomTypeNameMap); 
	    model.addAttribute("roomStatusMap", roomStatusMap);
	    
	    if (!errorMessage.isEmpty()) {
	        model.addAttribute("errorMessage", errorMessage);
	    }
	    
	    System.out.println("查詢完成，找到 " + fullResult.size() + " 筆資料");
	    return "back-end/room/searchRoom";
	}

// 顯示SearchRoom網頁
@GetMapping("/searchRoom")
public String showSearchRoom(@RequestParam(required = false) Integer roomId,
				            @RequestParam(required = false) Integer roomTypeId,
				            @RequestParam(required = false) Byte roomStatus,
				            Model model) {
	
	List<Room> roomList = roomService.findAll();

    // 創建房型名稱對應表（查詢結果需要顯示房型名稱）
    Map<Integer, String> roomTypeNameMap=roomTypeName.getRoomTypeNameMap();

	System.out.println("進入頁面");
	model.addAttribute("roomList", roomList);  
	model.addAttribute("roomStatusMap", roomStatusMap);  
	model.addAttribute("roomTypeNameMap", roomTypeNameMap);  
	return "/back-end/room/searchRoom";
	
	}

//顯示UpdateRoom網頁
@GetMapping("/updateRoom/{roomId}")
public String showUpdateRoom(@PathVariable Integer roomId, Model model) {
    System.out.println("要修改的房間ID: " + roomId);
	
	// 房型下拉選單選項
    List<RoomType> roomTypeList=roomTypeName.getRoomTypeNameList();

    // 根據 roomId 查詢房間資料
    Room room = roomService.findById(roomId);
    // 將房間資料傳給前端頁面
    System.out.println("進入頁面");
    model.addAttribute("room", room);
	model.addAttribute("roomTypeList", roomTypeList); 
	model.addAttribute("roomStatusMap", roomStatusMap); 
    return "/back-end/room/updateRoom";
}

	@PostMapping("/updateRoom")
	public String updateRoom(@Valid @ModelAttribute("room") Room room, 
							BindingResult result,
							Model model) {
	    List<String> errorMessages = new ArrayList<>();
		
	    // 房型下拉選單選項
	    List<RoomType> roomTypeList=roomTypeName.getRoomTypeNameList();
	    
	    model.addAttribute("roomTypeList", roomTypeList); 
		model.addAttribute("roomStatusMap", roomStatusMap); 
		
		// 後端驗證
		if (result.hasErrors()) {
			errorMessages = result.getFieldErrors().stream() 			 //取得錯誤資料集合
												.map(FieldError::getDefaultMessage)  //把每個錯誤轉成訊息
										        .collect(Collectors.toList());		 //收集成一個清單
			model.addAttribute("errorMessage", errorMessages);
			return "back-end/room/addRoom";
		}
				
		//檢查房型ID是否存在（避免外鍵錯誤）
		if(roomTypeService.findById(room.getRoomTypeId()) == null) {
			errorMessages.add("房型ID不存在，請重新輸入");
		}
		if(!errorMessages.isEmpty()) {
			model.addAttribute("errorMessage", errorMessages);
			model.addAttribute("room", room);
			return "/back-end/room/updateRoom";
		}else{
			// 取得原始房間資料以比較房型是否改變
			Room originalRoom = roomService.findById(room.getRoomId());
			Integer originalRoomTypeId = originalRoom.getRoomTypeId();
			
			roomService.updateRoom(room);
			
			//增加新房間後，更新"新"房型的統計資料
	        roomService.updateRoomTypeBasicStatistics(room.getRoomTypeId());
	        // 更新"新"房型的統計資料後改寫roomTypeAvailability資料庫
	        roomTypeAvailabilityService.recalculateAvailability(roomService.updateRoomTypeBasicStatistics(room.getRoomTypeId()));
			
			//增加新房間後，更新舊房型的統計資料
	        roomService.updateRoomTypeBasicStatistics(originalRoomTypeId);
	        // 更新舊房型的統計資料後改寫roomTypeAvailability資料庫
	        roomTypeAvailabilityService.recalculateAvailability(roomService.updateRoomTypeBasicStatistics(originalRoomTypeId));;
		}

		System.out.println("資料送出成功");
		model.addAttribute("successMessage", "房間資料更新成功！");
		model.addAttribute("room", room);
		return "back-end/room/updateRoom";
	}

	
	//刪除
	@GetMapping("/deleteRoom/{roomId}")
	public String deleteRoom(@PathVariable Integer roomId, 
						   @RequestHeader(value = "Referer", required = false) String referer,
						   RedirectAttributes redirectAttr) {
		 try {
		        // 在刪除前取得房間資料以獲取房型ID
		        Room roomToDelete = roomService.findById(roomId);
		        Integer roomTypeId = roomToDelete.getRoomTypeId();
		        
		        roomService.deleteRoom(roomId);
		        
		        // 刪除房間後，重新計算該房型的統計資料
		        roomTypeAvailabilityService.recalculateAvailability(roomService.updateRoomTypeBasicStatistics(roomTypeId));
		        
		        redirectAttr.addFlashAttribute("successMessage", "房間刪除成功！");
		    } catch (Exception e) { //外鍵約束錯誤會發生在控制器，service不用寫
		        redirectAttr.addFlashAttribute("errorMessage", "此房間尚有訂單資料，無法刪除");
		    }
		// 如果有上一頁就回去，否則回預設頁面
		if (referer != null) {
		return "redirect:" + referer;
		} else {
		return "redirect:/backend/room/listAllRoom";
		}
	}

}
