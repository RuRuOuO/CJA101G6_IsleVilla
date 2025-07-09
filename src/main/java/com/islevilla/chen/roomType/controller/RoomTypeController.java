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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhotoService;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.chen.util.map.RoomTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/backend/roomType")
public class RoomTypeController{
	
	@Autowired
	private RoomTypeName roomTypeName;
	@Autowired
	private RoomTypeService roomTypeService;
	@Autowired
	private RoomTypePhotoService roomTypePhotoService;
	
	private static final Map<Byte, String> saleStatusMap;

	static {
		saleStatusMap = new HashMap<>();
		saleStatusMap.put((byte)1, "上架中");
		saleStatusMap.put((byte)0, "下架中");
	}
	
	// 私有方法：準備頁面顯示所需的資料
	private void prepareModelForView(Model model) {
		List<RoomType> roomTypeList = roomTypeService.findAll();
		model.addAttribute("roomTypeList", roomTypeList);
		model.addAttribute("saleStatusMap", saleStatusMap);
		model.addAttribute("roomTypeNameList", roomTypeName.getRoomTypeNameList());
		model.addAttribute("roomTypeName", roomTypeName.getRoomTypeNameMap());
	}
	
	
//顯示網頁
@GetMapping("/listRoomType")
@PreAuthorize("hasAuthority('room')")
public String showListRoomType(
		@RequestParam(value = "roomTypeId", required = false) Integer roomTypeId,//先轉string方便做判斷 
        @RequestParam(value = "roomTypeSaleStatus", required = false) String roomTypeSaleStatus,
        Model model) {
	

    Byte saleStatus = null;
    if (roomTypeSaleStatus != null && !roomTypeSaleStatus.isEmpty()) {
        saleStatus = Byte.valueOf(roomTypeSaleStatus);
    }

    boolean hasRoomTypeId = roomTypeId != null && roomTypeId != 0;
    boolean hasSaleStatus = saleStatus != null;

    // 使用複合查詢來處理所有情況
    List<RoomType> roomTypeList = roomTypeService.compoundQuery(roomTypeId, saleStatus);
    
	System.out.println("進入頁面");
	System.out.println("從資料庫查詢到的 RoomType 筆數: " + roomTypeList.size()); // 檢查筆數
	// 如果 Model 中沒有 roomType 物件，就新增一個空的
	if (!model.containsAttribute("roomType")) {
		model.addAttribute("roomType", new RoomType());
	}
	model.addAttribute("selectedRoomTypeId", roomTypeId); 
	model.addAttribute("selectedSaleStatus", saleStatus); 
	model.addAttribute("roomTypeList", roomTypeList); 
	model.addAttribute("saleStatusMap", saleStatusMap);  //下拉選單
	model.addAttribute("roomTypeNameList", roomTypeName.getRoomTypeNameList());  //下拉選單
	model.addAttribute("roomTypeName", roomTypeName.getRoomTypeNameMap());
	model.addAttribute("sidebarActive", "listRoomType-list");
	return "back-end/roomType/listRoomType";
}
	

	//修改房型處理
	//newPhotos：接收前端上傳的新圖片檔案陣列
	//photoSortOrder：接收圖片排序資料的 JSON 字串
	//deletedPhotoIds：接收被刪除圖片 ID 的 JSON 字串
	@PostMapping("/updateRoomType")
	@PreAuthorize("hasAuthority('room')")
	public String UpdateRoomType(@Valid @ModelAttribute("roomType")RoomType roomType,
								BindingResult result,
								@RequestParam(value = "newPhotos", required = false) MultipartFile[] newPhotos,
								@RequestParam(value = "photoSortOrder", required = false) String photoSortOrder,
								@RequestParam(value = "deletedPhotoIds", required = false) String deletedPhotoIds,
								Model model,
								RedirectAttributes redirectAttributes) {
		System.out.println("處理更新房型請求，ID: " + roomType.getRoomTypeId());
		System.out.println("進入頁面");
		
		// 後端驗證
		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors().stream() 			 //取得錯誤資料集合
					.map(FieldError::getDefaultMessage)  //把每個錯誤轉成訊息
					.collect(Collectors.toList());		 //收集成一個清單
			// 重新載入頁面所需資料
			prepareModelForView(model);
			model.addAttribute("roomType", roomType);
			model.addAttribute("updateErrorMessage", errorMessages); 
			return "back-end/roomType/listRoomType";
		}
		try {
			// 更新房型基本資料
			roomTypeService.updateRoomType(roomType);
			
			// 處理圖片相關操作
			handleRoomTypePhotoOperations(roomType.getRoomTypeId(), newPhotos, photoSortOrder, deletedPhotoIds);
			
			redirectAttributes.addFlashAttribute("successMessage", "房型修改成功！");
		}catch(BusinessException e){
			// 重新載入頁面所需資料
			prepareModelForView(model);
			model.addAttribute("roomType", roomType);
			model.addAttribute("updateErrorMessage", List.of(e.getMessage()));
			
			return "back-end/roomType/listRoomType";
		}
		return "redirect:/backend/roomType/listRoomType";
	}

	//新增房型處理
	@PostMapping("/addRoomType")
	@PreAuthorize("hasAuthority('room')")
	public String addRoomType(@Valid @ModelAttribute("roomType")RoomType roomType,
								BindingResult result,
								@RequestParam(value = "newPhotos", required = false) MultipartFile[] newPhotos,
								Model model,
								RedirectAttributes redirectAttributes) {
		System.out.println("進入頁面");
		// 後端驗證
		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors().stream() 			 //取得錯誤資料集合
					.map(FieldError::getDefaultMessage)  //把每個錯誤轉成訊息
					.collect(Collectors.toList());		 //收集成一個清單
			// 重新載入頁面所需資料
			prepareModelForView(model);
			model.addAttribute("roomType", roomType);
			model.addAttribute("addErrorMessage", errorMessages);
			
			return "back-end/roomType/listRoomType";
		}
		
		try {
			// 新增房型
			RoomType savedRoomType = roomTypeService.addRoomType(roomType);
			
			// 處理新增的圖片
			//讓新增房型時也能同時上傳圖片
			if (newPhotos != null && newPhotos.length > 0) {
				for (MultipartFile file : newPhotos) {
					if (!file.isEmpty()) {
						// 檔案格式驗證
						String contentType = file.getContentType();
						if (contentType == null || !contentType.startsWith("image/")) {
							throw new BusinessException("不支援的檔案格式，請上傳圖片檔案");
						}
						
						// 檔案大小驗證 (5MB)
						if (file.getSize() > 5 * 1024 * 1024) {
							throw new BusinessException("檔案大小不能超過 5MB");
						}
						
						// 轉換為 Byte 陣列
						byte[] bytes = file.getBytes();
						Byte[] photoBytes = new Byte[bytes.length];
						for (int i = 0; i < bytes.length; i++) {
							photoBytes[i] = bytes[i];
						}
						
						// 新增圖片
						roomTypePhotoService.addRoomTypePhoto(savedRoomType.getRoomTypeId(), photoBytes, null);
					}
				}
			}
			
			redirectAttributes.addFlashAttribute("successMessage", "房型新增成功！");
		}catch(BusinessException e){
			// 重新載入頁面所需資料
			prepareModelForView(model);
			model.addAttribute("roomType", roomType);
			model.addAttribute("addErrorMessage", List.of(e.getMessage()));
			
			return "back-end/roomType/listRoomType";
		}catch(Exception e){
			// 重新載入頁面所需資料
			prepareModelForView(model);
			model.addAttribute("roomType", roomType);
			model.addAttribute("addErrorMessage", List.of("處理圖片時發生錯誤"));
			
			return "back-end/roomType/listRoomType";
		}
		return "redirect:/backend/roomType/listRoomType";
	}

	private void handleRoomTypePhotoOperations(Integer roomTypeId, MultipartFile[] newPhotos, String photoSortOrder, String deletedPhotoIds) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			
			// 1. 處理刪除的圖片
			//刪除使用者標記要刪除的圖片
			if (deletedPhotoIds != null && !deletedPhotoIds.isEmpty()) {
				List<Integer> idsToDelete = objectMapper.readValue(deletedPhotoIds, new TypeReference<List<Integer>>() {});
				for (Integer photoId : idsToDelete) {
					roomTypePhotoService.deleteRoomTypePhoto(photoId);
				}
			}
			
			// 2. 處理新增的圖片
			// 檔案驗證 + 轉換 + 新增到資料庫
			if (newPhotos != null && newPhotos.length > 0) {
				for (MultipartFile file : newPhotos) {
					if (!file.isEmpty()) {
						// 檔案格式驗證
						String contentType = file.getContentType();
						if (contentType == null || !contentType.startsWith("image/")) {
							throw new BusinessException("不支援的檔案格式，請上傳圖片檔案");
						}
						
						// 檔案大小驗證 (5MB)
						if (file.getSize() > 5 * 1024 * 1024) {
							throw new BusinessException("檔案大小不能超過 5MB");
						}
						
						// 轉換為 Byte 陣列
						byte[] bytes = file.getBytes();
						Byte[] photoBytes = new Byte[bytes.length];
						for (int i = 0; i < bytes.length; i++) {
							photoBytes[i] = bytes[i];
						}
						
						// 新增圖片（displayOrder 先設為 null，後面統一更新）
						roomTypePhotoService.addRoomTypePhoto(roomTypeId, photoBytes, null);
					}
				}
			}
			
			// 3. 處理排序更新
			//解析前端傳來的排序資料
			//只更新既有圖片的 displayOrder（新圖片的順序由資料庫自動處理）
			//這就是解決您圖片順序問題的核心邏輯！
			if (photoSortOrder != null && !photoSortOrder.isEmpty()) {
				List<Map<String, Object>> sortOrders = objectMapper.readValue(photoSortOrder, new TypeReference<List<Map<String, Object>>>() {});
				for (Map<String, Object> item : sortOrders) {
					Integer photoId = (Integer) item.get("id");
					Integer displayOrder = (Integer) item.get("displayOrder");
					Boolean isNew = (Boolean) item.get("isNew");
					
					// 只更新既有圖片的排序（新圖片的ID在新增時還不知道）
					if (photoId != null && !isNew) {
						roomTypePhotoService.updateDisplayOrder(photoId, displayOrder);
					}
				}
			}
			
		} catch (Exception e) {
			System.err.println("處理房型圖片操作時發生錯誤: " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException("處理房型圖片時發生錯誤: " + e.getMessage());
		}
	}
}

