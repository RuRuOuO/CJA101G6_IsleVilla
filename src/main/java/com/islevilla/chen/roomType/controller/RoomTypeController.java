package com.islevilla.chen.roomType.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.chen.util.map.RoomTypeName;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/backend/roomType")
public class RoomTypeController{
	
	@Autowired
	private RoomTypeName roomTypeName;
	@Autowired
	private RoomTypeService roomTypeService;
	
	private static final Map<Byte, String> saleStatusMap;

	static {
				saleStatusMap = new HashMap<>();
		saleStatusMap.put((byte)1, "上架中");
		saleStatusMap.put((byte)0, "下架中");
	}
	
//顯示網頁
@GetMapping("/listRoomType")
@PreAuthorize("hasAuthority('room')")
public String showListRoomType(
		@RequestParam(value = "roomTypeId", required = false) Integer roomTypeId,//先轉string方便做判斷 
        @RequestParam(value = "roomTypeSaleStatus", required = false) String roomTypeSaleStatus,
        Model model) {
	
    List<RoomType> roomTypeList;

    Byte saleStatus = null;
    if (roomTypeSaleStatus != null && !roomTypeSaleStatus.isEmpty()) {
        saleStatus = Byte.valueOf(roomTypeSaleStatus);
    }

    boolean hasRoomTypeId = roomTypeId != null && roomTypeId != 0;
    boolean hasSaleStatus = saleStatus != null;

    if (!hasRoomTypeId && !hasSaleStatus) {
        // 沒有任何條件：查詢全部
        roomTypeList = roomTypeService.findAll();
    } else if (hasRoomTypeId && hasSaleStatus) {
        // 兩個條件都存在：複合查詢
        roomTypeList = roomTypeService.compoundQuery(roomTypeId, saleStatus);
    } else if (hasRoomTypeId) {
        // 只有房型 ID 
    	RoomType result=roomTypeService.findById(roomTypeId);
    	roomTypeList = List.of(result); // Java 9+，將單筆轉成 List
    } else {
        // 只有上下架狀態
        roomTypeList = roomTypeService.findByRoomTypeSaleStatus(saleStatus);
    }
    
	System.out.println("進入頁面");
	System.out.println("從資料庫查詢到的 RoomType 筆數: " + roomTypeList.size()); // 檢查筆數
	model.addAttribute("roomType", new RoomType()); 
	model.addAttribute("selectedRoomTypeId", roomTypeId); 
	model.addAttribute("selectedSaleStatus", saleStatus); 
	model.addAttribute("roomTypeList", roomTypeList); 
	model.addAttribute("saleStatusMap", saleStatusMap);  //下拉選單
	model.addAttribute("roomTypeNameList", roomTypeName.getRoomTypeNameList());  //下拉選單
	model.addAttribute("roomTypeName", roomTypeName.getRoomTypeNameMap()); 
	return "back-end/roomType/listRoomType";
}
	

//顯示修改房型網頁
@GetMapping("/updateRoomType")
@PreAuthorize("hasAuthority('room')")
public String showUpdateRoomType(Model model) {

	System.out.println("進入頁面");
	model.addAttribute("roomType", new RoomType());  
	model.addAttribute("saleStatusMap", saleStatusMap);  //下拉選單
	return "/back-end/roomType/updateRoomType";
}

	//修改房型處理
	@PostMapping("/updateRoomType/{roomTypeId}")
	@PreAuthorize("hasAuthority('room')")
	public String UpdateRoomType(@Valid @ModelAttribute("roomType")RoomType roomType,
								BindingResult result,
								Model model,
								RedirectAttributes redirectAttributes) {
		System.out.println("處理更新房型請求，ID: " + roomType.getRoomTypeId());
		System.out.println("進入頁面");
		
		// 後端驗證
		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors().stream() 			 //取得錯誤資料集合
					.map(FieldError::getDefaultMessage)  //把每個錯誤轉成訊息
					.collect(Collectors.toList());		 //收集成一個清單
			model.addAttribute("roomType",roomType);  
			model.addAttribute("saleStatusMap", saleStatusMap);  //下拉選單
			model.addAttribute("errorMessage", errorMessages);
			return "back-end/roomType/updateRoomType";
		}
		try {
			roomTypeService.updateRoomType(roomType);
		}catch(BusinessException e){
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "/back-end/roomType/listRoomType";
	}

//顯示新增房型網頁
@GetMapping("/addRoomType")
@PreAuthorize("hasAuthority('room')")
public String showAddRoomType(Model model) {

	System.out.println("進入頁面");
	model.addAttribute("roomType", new RoomType());  
	model.addAttribute("saleStatusMap", saleStatusMap);  //下拉選單
	return "/back-end/roomType/addRoomType";
}

	//新增房型處理
	@PostMapping("/addRoomType")
	@PreAuthorize("hasAuthority('room')")
	public String addRoomType(@Valid @ModelAttribute("roomType")RoomType roomType,
								BindingResult result,
								Model model,
								RedirectAttributes redirectAttributes) {
		System.out.println("進入頁面");
		// 後端驗證
		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors().stream() 			 //取得錯誤資料集合
					.map(FieldError::getDefaultMessage)  //把每個錯誤轉成訊息
					.collect(Collectors.toList());		 //收集成一個清單
			model.addAttribute("roomType",roomType);  
			model.addAttribute("saleStatusMap", saleStatusMap);  //下拉選單
			model.addAttribute("errorMessage", errorMessages);
			return "back-end/roomType/updateRoomType";
		}
		
		try {
			roomTypeService.addRoomType(roomType);
		}catch(BusinessException e){
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "/back-end/roomType/listRoomType";
	}
}

