package com.islevilla.chen.roomTypePhoto.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhoto;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhotoService;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.chen.util.map.RoomTypeName;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/backend/roomTypePhoto")
public class RoomTypePhotoController {

	@Autowired
	private  RoomTypePhotoService roomTypePhotoService;
	@Autowired
	private  RoomTypeName roomTypeName;

//顯示selectRoomTypePhoto網頁
@GetMapping("/selectRoomTypePhoto")
public String showSelectPage(Model model) {
	RoomTypePhoto roomTypePhoto = new RoomTypePhoto();
	// 獲取所有房型供下拉選單使用
    List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
    
    System.out.println("進入頁面");
    model.addAttribute("roomTypePhoto", roomTypePhoto);
    model.addAttribute("roomTypeList", roomTypeList);
    return "/back-end/roomTypePhoto/selectRoomTypePhoto";
}
	    @PostMapping("/selectRoomTypePhoto")
		public String selectRoomTypePhotoPage(@RequestParam(required = false) Integer roomTypePhotoId,
					                        @RequestParam(required = false) Integer roomTypeId,
					                        Model model) {
	    	try {
		    	List<RoomTypePhoto> searchResult = roomTypePhotoService.compoundQuery(roomTypePhotoId,roomTypeId);
		    	System.out.println("查詢完成，找到 " + searchResult.size() + " 筆資料");
			    // 創建房型名稱對應表（查詢結果需要顯示房型名稱）
			    Map<Integer, String> roomTypeNameMap=roomTypeName.getRoomTypeNameMap();
			    // 將結果傳遞給頁面
			    model.addAttribute("searchResult", searchResult);
			    model.addAttribute("roomTypeNameMap", roomTypeNameMap);
			    return "back-end/roomTypePhoto/searchRoomTypePhoto";
	    	}catch(BusinessException e) {
		    	// 房型下拉選單選項
			    List<RoomType> roomTypeList=roomTypeName.getRoomTypeNameList();
			    model.addAttribute("roomTypeList", roomTypeList);
		    	model.addAttribute("errorMessage", e.getMessage());
		    	return "/back-end/roomTypePhoto/selectRoomTypePhoto";
		    }
	    }

// 顯示所有照片
@GetMapping("/listAllRoomTypePhoto")
public String showListAllRoomTypePhoto(Model model) {
	List<RoomTypePhoto> roomTypePhotoList = roomTypePhotoService.findAll();
    Map<Integer, String> roomTypeNameMap = roomTypeName.getRoomTypeNameMap();
    
    model.addAttribute("roomTypePhotoList", roomTypePhotoList); 
    model.addAttribute("roomTypeNameMap", roomTypeNameMap);
    return "/back-end/roomTypePhoto/listAllRoomTypePhoto";
}


//顯示AddRoomTypePhoto網頁
@GetMapping("/addRoomTypePhoto")
public String showAddRoomTypePhoto(Model model) {
	RoomTypePhoto roomTypePhoto = new RoomTypePhoto();
	List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
 
	System.out.println("進入新增房型照片頁面");
	model.addAttribute("roomTypePhoto", roomTypePhoto); 
	model.addAttribute("roomTypeList", roomTypeList); 
	return "/back-end/roomTypePhoto/addRoomTypePhoto";
}

	//處理網頁送出的請求
	@PostMapping("/addRoomTypePhoto")
	public String addRoomTypePhoto(@ModelAttribute("roomTypePhoto") RoomTypePhoto roomTypePhoto,@RequestParam("roomTypePhoto") MultipartFile file, Model model) {
		
	    List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
		model.addAttribute("roomTypeList", roomTypeList); 
		
		try {
		     // 檢查檔案大小（5MB = 5*1024*1024 bytes）
	        long maxSize = 5 * 1024 * 1024; // 5MB
			if (file.getSize() > maxSize) {
				throw new BusinessException("檔案大小不能超過 5MB!");
			}
			
			// 檢查檔案格式
			String contentType = file.getContentType();
			List<String> allowedTypes = Arrays.asList(
				"image/jpeg", "image/jpg", "image/png", "image/gif"
			);
			if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
				throw new BusinessException("不支援的檔案格式，請上傳 JPG、PNG 或 GIF 格式的圖片");
			}
			
			//將MultipartFile 轉換為Byte[] 陣列
			byte[] bytes = file.getBytes();
			Byte[] photoBytes = new Byte[bytes.length];  //包裝型別陣列
			
			for (int i = 0; i < bytes.length; i++) {
				photoBytes[i] = bytes[i];
			}
			
	        roomTypePhotoService.addRoomTypePhoto(roomTypePhoto.getRoomType().getRoomTypeId(), 
	        										photoBytes, 
										        	roomTypePhoto.getDisplayOrder());

			System.out.println("房型照片新增成功");
			model.addAttribute("successMessage", "房型照片新增成功！");
			model.addAttribute("roomTypePhoto", roomTypePhoto); 
			return "back-end/roomTypePhoto/addRoomTypePhoto";
	    } catch (BusinessException e) {
	        model.addAttribute("errorMessage", e.getMessage());
	        return "/back-end/roomTypePhoto/addRoomTypePhoto";
	    }catch (IOException e) {
	        model.addAttribute("errorMessage", "檔案處理失敗，請重新上傳");
	        return "/back-end/roomTypePhoto/addRoomTypePhoto";
	    }
	}
	
//顯示UpdateRoomTypePhoto網頁
@GetMapping("/updateRoomTypePhoto/{roomTypePhotoId}")
public String showUpdateRoomTypePhoto(@PathVariable Integer roomTypePhotoId, Model model) {
    System.out.println("要修改的房型照片ID: " + roomTypePhotoId);
	
	// 房型下拉選單選項
    List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();

    // 根據 roomTypePhotoId 查詢房型照片資料
    RoomTypePhoto roomTypePhoto = roomTypePhotoService.findById(roomTypePhotoId);
    // 將房型照片資料傳給前端頁面
    System.out.println("進入修改頁面");
    model.addAttribute("roomTypePhoto", roomTypePhoto);
	model.addAttribute("roomTypeList", roomTypeList); 
    return "/back-end/roomTypePhoto/updateRoomTypePhoto";
}
	@PostMapping("/updateRoomTypePhoto")
	public String updateRoomTypePhoto(@ModelAttribute("roomTypePhoto") RoomTypePhoto roomTypePhoto,
									@RequestParam(value = "roomTypePhoto", required = false) MultipartFile file,
									Model model) {
	    // 房型下拉選單選項
	    List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
	    model.addAttribute("roomTypeList", roomTypeList); 
		
		try {
			// 只有在有上傳更新檔案時才檢查和處理檔案
			// 如果沒有新檔案，照片欄位保持原本的值，不會被覆蓋
			if (file != null && !file.isEmpty()) {
				 // 檢查檔案大小（5MB = 5*1024*1024 bytes）
		        long maxSize = 5 * 1024 * 1024; // 5MB
				if (file.getSize() > maxSize) {
					throw new BusinessException("檔案大小不能超過 5MB!");
				}
				
				// 檢查檔案格式
				String contentType = file.getContentType();
				List<String> allowedTypes = Arrays.asList(
					"image/jpeg", "image/jpg", "image/png", "image/gif"
				);
				if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
					throw new BusinessException("不支援的檔案格式，請上傳 JPG、PNG 或 GIF 格式的圖片");
				}
				
				//將MultipartFile 轉換為Byte[] 陣列
				byte[] bytes = file.getBytes();
				Byte[] photoBytes = new Byte[bytes.length];  //包裝型別陣列
				//byte ➜ Byte（自動裝箱）
				for (int i = 0; i < bytes.length; i++) {
					photoBytes[i] = bytes[i];
				}
				roomTypePhoto.setRoomTypePhoto(photoBytes);
			}
			
			roomTypePhotoService.updateRoomTypePhoto(roomTypePhoto);
			System.out.println("房型照片更新成功");
			model.addAttribute("successMessage", "房型照片更新成功！");
			return "redirect:/backend/roomTypePhoto/listAllRoomTypePhoto";
		} catch (BusinessException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "/back-end/roomTypePhoto/updateRoomTypePhoto";
		} catch (IOException e) {
	        model.addAttribute("errorMessage", "檔案處理失敗，請重新上傳");
	        return "/back-end/roomTypePhoto/updateRoomTypePhoto";
	    }
	}
	
	//更新顯示順序
	@PostMapping("/updateDisplayOrder")
	public String updateDisplayOrder(@RequestParam Integer roomTypePhotoId, 
									@RequestParam Integer newDisplayOrder,
									RedirectAttributes redirectAttr) {
		roomTypePhotoService.updateDisplayOrder(roomTypePhotoId, newDisplayOrder);
		return "redirect:/backend/roomTypePhoto/listAllRoomTypePhoto";
	}

// 刪除房型照片
@GetMapping("/deleteRoomTypePhoto/{roomTypePhotoId}")
public String deleteRoomTypePhoto(@PathVariable Integer roomTypePhotoId, 
						   @RequestHeader(value = "Referer", required = false) String referer,
						   RedirectAttributes redirectAttr) {
	try {
		roomTypePhotoService.deleteRoomTypePhoto(roomTypePhotoId);
		System.out.println("房型照片刪除成功，ID: " + roomTypePhotoId);
		redirectAttr.addFlashAttribute("successMessage", "房型照片刪除成功！");
	} catch (BusinessException e) {
		redirectAttr.addFlashAttribute("errorMessage", e.getMessage());
	}
	
	// 如果有來源頁面，則返回來源頁面，否則返回列表頁面
	if (referer != null && !referer.isEmpty()) {
		return "redirect:" + referer;
	} else {
		return "redirect:/backend/roomTypePhoto/listAllRoomTypePhoto";
	}
}

//顯示房型照片
@GetMapping("/image/{roomTypePhotoId}")
public ResponseEntity<byte[]> getRoomTypePhotoImage(@PathVariable Integer roomTypePhotoId) {
	try {
	     RoomTypePhoto roomTypePhoto = roomTypePhotoService.findById(roomTypePhotoId);
	     if (roomTypePhoto != null && roomTypePhoto.getRoomTypePhoto() != null) {
	         // 將 Byte[] 轉換為 byte[]
	         Byte[] photoBytes = roomTypePhoto.getRoomTypePhoto();
	         byte[] imageBytes = new byte[photoBytes.length];
	         for (int i = 0; i < photoBytes.length; i++) {
	             imageBytes[i] = photoBytes[i];
	         }
	         
	         return ResponseEntity.ok()
	                 .header("Content-Type", "image/png") // 預設為jpeg，實際應根據檔案類型判斷
	                 .body(imageBytes);
	     }else{
	    	// 找不到，回傳預設圖片
	    	 InputStream defaultImageStream = getClass().getResourceAsStream("/static/img/roomTypePhoto/noPicture.png");
		    if (defaultImageStream != null) {
		        byte[] defaultImageBytes = defaultImageStream.readAllBytes();
		        return ResponseEntity.ok()
		                .header("Content-Type", "image/*")
		                .body(defaultImageBytes);
		    } else {
		        // 如果連預設圖片都找不到，返回錯誤訊息
		        return ResponseEntity.ok()
		                .header("Content-Type", "text/plain; charset=UTF-8")
		                .body("找不到預設圖片，請聯絡管理員".getBytes(StandardCharsets.UTF_8));
		    }
	     }
     }catch (Exception e) {
		 return ResponseEntity.ok()
	             .header("Content-Type", "text/plain; charset=UTF-8")
	             .body("找不到預設圖片，請聯絡管理員".getBytes(StandardCharsets.UTF_8));
     }
} 
}
