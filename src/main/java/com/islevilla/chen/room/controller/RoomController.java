package com.islevilla.chen.room.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.room.model.RoomService;
import com.islevilla.chen.room.model.RoomTypeCount; // 引入 RoomTypeCount
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityService;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.chen.util.map.RoomTypeName;
import com.islevilla.wei.PageUtil;

import jakarta.servlet.http.HttpServletRequest;
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
		List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
	    System.out.println("進入頁面");
		model.addAttribute("room", room);
		model.addAttribute("roomTypeList", roomTypeList);
		model.addAttribute("roomStatusMap", roomStatusMap);
		return "/back-end/room/addRoom";
	}
	//處理網頁送出的請求
	@PostMapping("/addRoom")
	public String addRoom(@Valid @ModelAttribute("room") Room room, BindingResult result, Model model) {
		// 顯示AddRoom網頁
		List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
		model.addAttribute("roomTypeList", roomTypeList);
		model.addAttribute("roomStatusMap", roomStatusMap);
        // 後端驗證
		if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()	 //取得錯誤資料集合
                    .map(FieldError::getDefaultMessage) 					 //把每個錯誤轉成訊息
                    .collect(Collectors.toList());         					//收集成一個清單
			model.addAttribute("errorMessage", errorMessages);
			return "back-end/room/addRoom";
		}

		try {
			// 【重構】1. 新增房間。addRoom 內部已包含清除 DTO 快取的邏輯。
			roomService.addRoom(room);

			// 【重構】2. 使用新的 getRoomTypeStatistics 方法獲取 DTO (此方法會利用快取)。
			RoomTypeCount stats = roomService.getRoomTypeStatistics(room.getRoomTypeId());

			// 【重構】3. 將獲取到的 DTO 傳遞給下一個服務。
			roomTypeAvailabilityService.recalculateAvailability(stats);

			System.out.println("資料送出成功");
			model.addAttribute("successMessage", "房間新增成功！");
			model.addAttribute("room", new Room()); // 清空表單
		} catch (BusinessException e) {
            // 統一處理例外，顯示給使用者
			List<String> errorMessages = new ArrayList<>();
			errorMessages.add(e.getMessage());
			model.addAttribute("errorMessage", errorMessages);
		}
		return "/back-end/room/addRoom";
	}

	// 顯示SelectPage網頁
	@GetMapping("/selectRoomPage")
	public String showSelectPage(@RequestParam(defaultValue = "0") int page,
								 @RequestParam(defaultValue = "10") int size,
								 Model model,
								 HttpServletRequest request) {
		Room room = new Room();
	    // 房型下拉選單選項
		List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
	    // 分頁查詢
		Pageable pageable = PageRequest.of(page, size, Sort.by("roomId").ascending());
		Page<Room> roomPage = roomService.findAll(pageable);
	    // 使用 PageUtil 將分頁資料加入 model
		PageUtil.ModelWithPage(roomPage, model, page, "roomList", request);
	    // 創建房型名稱對應表
		Map<Integer, String> roomTypeNameMap = roomTypeName.getRoomTypeNameMap();
	    System.out.println("進入頁面");
		model.addAttribute("room", room);
		model.addAttribute("roomTypeList", roomTypeList);
		model.addAttribute("roomStatusMap", roomStatusMap);
	    // 將房型名稱對應表加到 model
		model.addAttribute("roomTypeNameMap", roomTypeNameMap);
		model.addAttribute("sidebarActive", "selectRoomPage-list");
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
		List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
        // 創建房型名稱對應表（查詢結果需要顯示房型名稱）
		Map<Integer, String> roomTypeNameMap = roomTypeName.getRoomTypeNameMap();
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
	//顯示UpdateRoom網頁
	@GetMapping("/updateRoom/{roomId}")
	public String showUpdateRoom(@PathVariable Integer roomId, Model model) {
		System.out.println("要修改的房間ID: " + roomId);
	    // 房型下拉選單選項
		List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
	    // 根據 roomId 查詢房間資料
		roomService.findById(roomId).ifPresent(room -> model.addAttribute("room", room));
	    // 將房間資料傳給前端頁面
		System.out.println("進入頁面");
		model.addAttribute("roomTypeList", roomTypeList);
		model.addAttribute("roomStatusMap", roomStatusMap);
		return "/back-end/room/updateRoom";
	}

	@PostMapping("/updateRoom")
	public String updateRoom(@Valid @ModelAttribute("room") Room room,
							 BindingResult result,
							 Model model) {
        // 房型下拉選單選項
		List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
		model.addAttribute("roomTypeList", roomTypeList);
		model.addAttribute("roomStatusMap", roomStatusMap);
        // 後端驗證
		if (result.hasErrors()) {
			model.addAttribute("errorMessage", result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()));
			return "back-end/room/updateRoom";
		}

		try {
			// 取得原始房間資料以比較房型是否改變
			Room originalRoom = roomService.findById(room.getRoomId()).orElse(null);
			if (originalRoom == null) {
				throw new BusinessException("找不到要更新的房間，ID: " + room.getRoomId());
			}
			Integer originalRoomTypeId = originalRoom.getRoomTypeId();

			// 【重構】1. 更新房間。updateRoom 內部已包含清除新舊房型 DTO 快取的邏輯。
			roomService.updateRoom(room);
			// 如果房型被更改，舊房型的快取也需要被清除
			if (!originalRoomTypeId.equals(room.getRoomTypeId())) {
				roomService.invalidateRoomTypeStatsCache(originalRoomTypeId);
			}

			// 【重構】2. 獲取新房型的統計資料
			RoomTypeCount newStats = roomService.getRoomTypeStatistics(room.getRoomTypeId());
			roomTypeAvailabilityService.recalculateAvailability(newStats);

			// 【重構】3. 如果房型已變更，也需要重新計算舊房型的庫存
			if (!originalRoomTypeId.equals(room.getRoomTypeId())) {
				RoomTypeCount oldStats = roomService.getRoomTypeStatistics(originalRoomTypeId);
				roomTypeAvailabilityService.recalculateAvailability(oldStats);
			}

			model.addAttribute("successMessage", "房間資料更新成功！");
		} catch (BusinessException e) {
			model.addAttribute("errorMessage", List.of(e.getMessage()));
		}
		
		model.addAttribute("room", room);
		return "back-end/room/updateRoom";
	}

	@GetMapping("/deleteRoom/{roomId}")
	public String deleteRoom(@PathVariable Integer roomId,
							 @RequestHeader(value = "Referer", required = false) String referer,
							 RedirectAttributes redirectAttr) {
		try {
			// 在刪除前取得房間資料以獲取房型ID
			Room roomToDelete = roomService.findById(roomId)
					.orElseThrow(() -> new BusinessException("找不到要刪除的房間，ID: " + roomId));
			Integer roomTypeId = roomToDelete.getRoomTypeId();

			// 【重構】1. 刪除房間。deleteRoom 內部已包含清除 DTO 快取的邏輯。
			roomService.deleteRoom(roomId);

			// 【重構】2. 獲取更新後的統計 DTO
			RoomTypeCount stats = roomService.getRoomTypeStatistics(roomTypeId);

			// 【重構】3. 觸發每日庫存的重新計算
			roomTypeAvailabilityService.recalculateAvailability(stats);

			redirectAttr.addFlashAttribute("successMessage", "房間刪除成功！");
        } catch (Exception e) { //外鍵約束錯誤會發生在控制器，service不用寫
			redirectAttr.addFlashAttribute("errorMessage", "此房間尚有訂單資料，無法刪除");
		}
        // 如果有上一頁就回去，否則回預設頁面
		return "redirect:" + (referer != null ? referer : "/backend/room/selectRoomPage");
	}
}