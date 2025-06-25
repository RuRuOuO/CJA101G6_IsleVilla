package com.islevilla.chen.roomTypePhoto.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhoto;
import com.islevilla.chen.roomTypePhoto.model.RoomTypePhotoService;
import com.islevilla.chen.util.map.RoomTypeName;

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
	    public String selectRoomTypePhoto(@ModelAttribute("roomTypePhoto") RoomTypePhoto roomTypePhoto, Model model) {
	    	List<String> errorMessage = new ArrayList<>();
	    	List<RoomTypePhoto> searchResult = roomTypePhotoService.compoundQuery(roomTypePhoto.getRoomTypePhotoId(), roomTypePhoto.getRoomType().getRoomTypeId());
	    	if (searchResult.isEmpty()) {
	            errorMessage.add("查無符合條件的房間資料");
	        }
	        
	        // 房型下拉選單選項
		    List<RoomType> roomTypeList=roomTypeName.getRoomTypeNameList();
		    // 創建房型名稱對應表（查詢結果需要顯示房型名稱）
		    Map<Integer, String> roomTypeNameMap=roomTypeName.getRoomTypeNameMap();
		    // 將結果傳遞給頁面
		    model.addAttribute("searchResult", searchResult);
		    model.addAttribute("roomTypeList", roomTypeList);
		    model.addAttribute("roomTypeNameMap", roomTypeNameMap);
		    
		    if (!errorMessage.isEmpty()) {
		        model.addAttribute("errorMessage", errorMessage);
		        return "/back-end/roomTypePhoto/selectRoomTypePhoto";
		    }
		    
		    System.out.println("查詢完成，找到 " + searchResult.size() + " 筆資料");
		    return "back-end/roomTypePhoto/searchRoomTypePhoto";
	    }
}	    
////顯示searchRoomTypePhoto網頁
//@GetMapping("/searchRoomTypePhoto")
//public String getOne(@RequestParam("roomTypePhotoId") String roomTypePhotoIdStr, 
//                    Model model, RedirectAttributes redirectAttributes) {
//    
//    List<String> errorMsg = new ArrayList<>();
//    
//    // 驗證輸入
//    if (roomTypePhotoIdStr == null || roomTypePhotoIdStr.trim().isEmpty()) {
//        errorMsg.add("沒輸入數字，請輸入圖片編號");
//    }
//    
//    if (!errorMsg.isEmpty()) {
//        redirectAttributes.addFlashAttribute("errorMsg", errorMsg);
//        return "redirect:/room-type-photo/select";
//    }
//    
//    Integer roomTypePhotoId;
//    try {
//        roomTypePhotoId = Integer.valueOf(roomTypePhotoIdStr);
//    } catch (NumberFormatException e) {
//        errorMsg.add("輸入不是數字格式，請重新輸入");
//        redirectAttributes.addFlashAttribute("errorMsg", errorMsg);
//        return "redirect:/room-type-photo/select";
//    }
//}
//}
//	        
//	        // 查詢資料
//	        Optional<RoomTypePhoto> photoOpt = roomTypePhotoService.getOne(roomTypePhotoId);
//	        if (!photoOpt.isPresent()) {
//	            errorMsg.add("查無此資料");
//	            redirectAttributes.addFlashAttribute("errorMsg", errorMsg);
//	            return "redirect:/room-type-photo/select";
//	        }
//	        
//	        model.addAttribute("rtpVO", photoOpt.get());
//	        return "room_type_photo/list_getOne";
//	    }
//	    
//	    @GetMapping("/getByRoomTypeId")
//	    public String getByRoomTypeId(@RequestParam("roomTypeId") Integer roomTypeId, Model model) {
//	        Set<RoomTypePhoto> photoSet = roomTypePhotoService.findByRoomTypeId(roomTypeId);
//	        model.addAttribute("RoomTypePhotoVOSet", photoSet);
//	        return "room_type_photo/list_getOne";
//	    }
//	    
//	    @GetMapping("/getOneForUpdate")
//	    public String getOneForUpdate(@RequestParam("roomTypePhotoId") Integer roomTypePhotoId, 
//	                                 Model model, RedirectAttributes redirectAttributes) {
//	        
//	        List<String> errorMsg = new ArrayList<>();
//	        Optional<RoomTypePhoto> photoOpt = roomTypePhotoService.getOne(roomTypePhotoId);
//	        
//	        if (!photoOpt.isPresent()) {
//	            errorMsg.add("查無此資料");
//	            redirectAttributes.addFlashAttribute("errorMsg", errorMsg);
//	            return "redirect:/room-type-photo/select";
//	        }
//	        
//	        model.addAttribute("rtpVO", photoOpt.get());
//	        return "room_type_photo/update_input";
//	    }
	    
//	    @GetMapping("/showImage")
//	    public ResponseEntity<byte[]> showImage(@RequestParam("roomTypePhotoId") Integer roomTypePhotoId) {
//	        Optional<RoomTypePhoto> photoOpt = roomTypePhotoService.getOne(roomTypePhotoId);
//	        
//	        if (photoOpt.isPresent() && photoOpt.get().getRoomTypePhoto() != null) {
//	            byte[] photo = photoOpt.get().getRoomTypePhoto();
//	            HttpHeaders headers = new HttpHeaders();
//	            headers.setContentType(MediaType.IMAGE_JPEG);
//	            return new ResponseEntity<>(photo, headers, HttpStatus.OK);
//	        }
//	        
//	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//	    }
//	    
//	    @PostMapping("/update")
//	    public String update(@RequestParam("roomTypePhotoId") Integer roomTypePhotoId,
//	                        @RequestParam("roomTypeId") String roomTypeIdStr,
//	                        @RequestParam("roomTypePhoto") MultipartFile file,
//	                        Model model, RedirectAttributes redirectAttributes) throws IOException {
//	        
//	        List<String> errorMsg = new ArrayList<>();
//	        
//	        // 驗證房型ID
//	        Integer roomTypeId = null;
//	        if (roomTypeIdStr == null || roomTypeIdStr.trim().isEmpty()) {
//	            errorMsg.add("房型圖片編號: 請勿空白");
//	        } else {
//	            try {
//	                roomTypeId = Integer.valueOf(roomTypeIdStr);
//	                Set<RoomTypePhoto> photoSet = roomTypePhotoService.findByRoomTypeId(roomTypeId);
//	                if (photoSet.isEmpty()) {
//	                    errorMsg.add("查無此房型圖片");
//	                }
//	            } catch (NumberFormatException e) {
//	                errorMsg.add("房型圖片編號: 只能是數字");
//	                roomTypeId = 0;
//	            }
//	        }
//	        
//	        // 處理圖片
//	        byte[] photo;
//	        if (!file.isEmpty()) {
//	            photo = file.getBytes();
//	        } else {
//	            // 如果沒有上傳新圖片，保持原有圖片
//	            Optional<RoomTypePhoto> existingPhoto = roomTypePhotoService.getOne(roomTypePhotoId);
//	            photo = existingPhoto.map(RoomTypePhoto::getRoomTypePhoto).orElse(null);
//	        }
//	        
//	        RoomTypePhoto roomTypePhoto = new RoomTypePhoto(roomTypePhotoId, roomTypeId, photo);
//	        
//	        if (!errorMsg.isEmpty()) {
//	            model.addAttribute("rtpVO", roomTypePhoto);
//	            model.addAttribute("errorMsg", errorMsg);
//	            return "room_type_photo/update_input";
//	        }
//	        
//	        // 更新資料
//	        RoomTypePhoto updatedPhoto = roomTypePhotoService.update(roomTypePhotoId, roomTypeId, photo);
//	        model.addAttribute("rtpVO", updatedPhoto);
//	        return "room_type_photo/list_getOne";
//	    }
//	    
//	    @GetMapping("/add")
//	    public String showAddPage() {
//	        return "room_type_photo/add";
//	    }
//	    
//	    @PostMapping("/insert")
//	    public String insert(@RequestParam("roomTypeId") String roomTypeIdStr,
//	                        @RequestParam("roomTypePhoto") MultipartFile file,
//	                        Model model, RedirectAttributes redirectAttributes) throws IOException {
//	        
//	        List<String> errorMsg = new ArrayList<>();
//	        
//	        // 驗證房型ID
//	        Integer roomTypeId = null;
//	        if (roomTypeIdStr == null || roomTypeIdStr.trim().isEmpty()) {
//	            errorMsg.add("房型圖片編號: 請勿空白");
//	        } else {
//	            try {
//	                roomTypeId = Integer.valueOf(roomTypeIdStr);
//	                Set<RoomTypePhoto> photoSet = roomTypePhotoService.findByRoomTypeId(roomTypeId);
//	                if (photoSet.isEmpty()) {
//	                    errorMsg.add("查無此房型圖片");
//	                }
//	            } catch (NumberFormatException e) {
//	                errorMsg.add("房型圖片編號: 只能是數字");
//	                roomTypeId = 0;
//	            }
//	        }
//	        
//	        // 處理圖片
//	        byte[] photo = file.getBytes();
//	        RoomTypePhoto roomTypePhoto = new RoomTypePhoto(null, roomTypeId, photo);
//	        
//	        if (!errorMsg.isEmpty()) {
//	            model.addAttribute("rtpVO", roomTypePhoto);
//	            model.addAttribute("errorMsg", errorMsg);
//	            return "room_type_photo/add";
//	        }
//	        
//	        // 新增資料
//	        roomTypePhotoService.add(roomTypeId, photo);
//	        return "redirect:/room-type-photo/listAll";
//	    }
//	    
//	    @PostMapping("/delete")
//	    public String delete(@RequestParam("roomTypePhotoId") Integer roomTypePhotoId) {
//	        roomTypePhotoService.delete(roomTypePhotoId);
//	        return "redirect:/room-type-photo/listAll";
//	    }
//	    
//	    @GetMapping("/listAll")
//	    public String listAll(Model model) {
//	        List<RoomTypePhoto> photos = roomTypePhotoService.getAll();
//	        model.addAttribute("photos", photos);
//	        return "room_type_photo/listAll";
//	    }
