package com.islevilla.chen.roomType.controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhoto;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhotoService;

@Controller
@RequestMapping("/roomtype")
public class FrontRoomTypeController {
	
	@Autowired
	private RoomTypeService roomTypeService;
	
	@Autowired
	private RoomTypePhotoService roomTypePhotoService;
	
	// 前端房型展示頁面
	@GetMapping
	public String showRoomTypes(Model model) {
		// 只顯示上架中的房型
		List<RoomType> roomTypeList = roomTypeService.findByRoomTypeSaleStatus((byte)1);
		
		// 為每個房型載入第一張圖片
		Map<Integer, String> roomTypeFirstPhoto = new HashMap<>();
		for (RoomType roomType : roomTypeList) {
			List<RoomTypePhoto> photos = roomTypePhotoService.roomTypeFindPhotos(roomType.getRoomTypeId());
			if (!photos.isEmpty()) {
				roomTypeFirstPhoto.put(roomType.getRoomTypeId(), photos.get(0).getRoomTypePhotoId().toString());
			}
		}
		
		model.addAttribute("roomTypeList", roomTypeList);
		model.addAttribute("roomTypeFirstPhoto", roomTypeFirstPhoto);
		return "front-end/roomtype/roomtype";
	}
	
	// 公開的房型圖片API端點（不需要認證）
	@GetMapping("/image/{roomTypePhotoId}")
	public ResponseEntity<byte[]> getRoomTypeImage(@PathVariable Integer roomTypePhotoId) {
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
						.header("Content-Type", "image/png")
						.body(imageBytes);
			} else {
				// 找不到圖片，回傳預設圖片或錯誤訊息
				try {
					InputStream defaultImageStream = getClass().getResourceAsStream("/static/img/roomTypePhoto/noPicture.png");
					if (defaultImageStream != null) {
						byte[] defaultImageBytes = defaultImageStream.readAllBytes();
						return ResponseEntity.ok()
								.header("Content-Type", "image/*")
								.body(defaultImageBytes);
					}
				} catch (Exception e) {
					// 忽略預設圖片載入錯誤
				}
				
				// 回傳錯誤訊息
				return ResponseEntity.ok()
						.header("Content-Type", "text/plain; charset=UTF-8")
						.body("圖片不存在".getBytes(StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			return ResponseEntity.ok()
					.header("Content-Type", "text/plain; charset=UTF-8")
					.body("載入圖片時發生錯誤".getBytes(StandardCharsets.UTF_8));
		}
	}
	
	// 獲取房型所有圖片列表
	@GetMapping("/{roomTypeId}/images")
	@ResponseBody
	public ResponseEntity<List<Map<String, Object>>> getRoomTypeImages(@PathVariable Integer roomTypeId) {
		try {
			List<RoomTypePhoto> photos = roomTypePhotoService.roomTypeFindPhotos(roomTypeId);
			
			if (photos == null || photos.isEmpty()) {
				return ResponseEntity.ok(new ArrayList<>());
			}
			
			List<Map<String, Object>> imageList = new ArrayList<>();
			for (RoomTypePhoto photo : photos) {
				// 只加入有圖片資料的項目
				if (photo.getRoomTypePhoto() != null) {
					Map<String, Object> imageInfo = new HashMap<>();
					imageInfo.put("photoId", photo.getRoomTypePhotoId());
					imageInfo.put("displayOrder", photo.getDisplayOrder());
					imageInfo.put("hasImage", true);
					imageList.add(imageInfo);
				}
			}
			
			// 按照顯示順序排序
			imageList.sort((a, b) -> {
				Integer orderA = (Integer) a.get("displayOrder");
				Integer orderB = (Integer) b.get("displayOrder");
				return Integer.compare(orderA != null ? orderA : 0, orderB != null ? orderB : 0);
			});
			
			return ResponseEntity.ok(imageList);
		} catch (Exception e) {
			System.err.println("取得房型圖片列表失敗: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
} 