package com.islevilla.patty.roompromotionprice.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.patty.promotion.model.Promotion;
import com.islevilla.patty.promotion.model.PromotionService;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPrice;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPriceService;
import com.islevilla.patty.roompromotionprice.model.RoomPromotionPriceId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/backend/roompromotionprice")
@RequiredArgsConstructor
public class RoomPromotionPriceController {

    private final RoomPromotionPriceService roomPromotionPriceSvc;
    private final PromotionService promotionService;
    private final RoomTypeService roomTypeService;

    @GetMapping("/add")
    public String addRoomPromotionPrice(ModelMap model) {
        model.addAttribute("roomPromotionPrice", new RoomPromotionPrice());
        model.addAttribute("promotionListData", promotionService.getAll());
        model.addAttribute("roomTypeListData", roomTypeService.findAll());
        return "back-end/roompromotionprice/addRoomPromotionPrice";
    }

    @PostMapping("/insert")
    public String insert(@Valid @ModelAttribute RoomPromotionPrice roomPromotionPrice, BindingResult result, ModelMap model) throws IOException {
        if (result.hasErrors()) {
            // 重新載入下拉選單資料
            model.addAttribute("promotionListData", promotionService.getAll());
            model.addAttribute("roomTypeListData", roomTypeService.findAll());
            return "back-end/roompromotionprice/addRoomPromotionPrice";
        }
        
        // 設定複合主鍵
        RoomPromotionPriceId id = new RoomPromotionPriceId();
        id.setRoomPromotionId(roomPromotionPrice.getPromotion().getRoomPromotionId());
        id.setRoomTypeId(roomPromotionPrice.getRoomType().getRoomTypeId());
        roomPromotionPrice.setId(id);
        
        roomPromotionPriceSvc.addRoomPromotionPrice(roomPromotionPrice);
        return "redirect:/backend/roompromotionprice/listAll";
    }

    @PostMapping("/getOneForUpdate")
    public String getOneForUpdate(@RequestParam("roomPromotionId") Integer id, ModelMap model) {
        // 獲取該優惠專案的所有價格設定
        List<RoomPromotionPrice> existingPriceSettings = roomPromotionPriceSvc.getAllWithDetails().stream()
            .filter(rpp -> rpp.getRoomPromotionId().equals(id))
            .collect(Collectors.toList());
        
        // 傳遞現有的價格設定
        model.addAttribute("existingPriceSettings", existingPriceSettings);
        
        // 創建一個空的物件用於新增
        RoomPromotionPrice emptyRpp = new RoomPromotionPrice();
        Promotion promotion = promotionService.getOnePromotion(id);
        emptyRpp.setPromotion(promotion);
        RoomPromotionPriceId emptyId = new RoomPromotionPriceId();
        emptyId.setRoomPromotionId(id);
        emptyId.setRoomTypeId(0);
        emptyRpp.setId(emptyId);
        model.addAttribute("roomPromotionPrice", emptyRpp);
        
        // 獲取所有房型
        List<RoomType> allRoomTypes = roomTypeService.findAll();
        
        // 獲取已設定價格的房型ID列表
        List<Integer> assignedRoomTypeIds = existingPriceSettings.stream()
            .map(rpp -> rpp.getRoomTypeId())
            .collect(Collectors.toList());
        
        // 過濾出未分配的房型
        List<RoomType> unassignedRoomTypes = allRoomTypes.stream()
            .filter(roomType -> !assignedRoomTypeIds.contains(roomType.getRoomTypeId()))
            .collect(Collectors.toList());
        
        // 折扣率選項 0.05~1.00
        java.util.List<Double> discountOptions = new java.util.ArrayList<>();
        for (double i = 0.05; i <= 1.0001; i += 0.05) {
            discountOptions.add(Math.round(i * 100.0) / 100.0);
        }
        model.addAttribute("discountOptions", discountOptions);
        
        // 傳遞房型列表供下拉選單使用
        model.addAttribute("roomTypeListData", allRoomTypes);
        model.addAttribute("unassignedRoomTypeList", unassignedRoomTypes);
        
        return "back-end/roompromotionprice/update_roompromotionprice_input";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute RoomPromotionPrice roomPromotionPrice, BindingResult result, ModelMap model) throws IOException {
        if (result.hasErrors()) {
            // 重新載入房型列表和未分配房型列表
            Integer promotionId = roomPromotionPrice.getPromotion().getRoomPromotionId();
            
            // 獲取該優惠專案的所有價格設定
            List<RoomPromotionPrice> existingPriceSettings = roomPromotionPriceSvc.getAllWithDetails().stream()
                .filter(rpp -> rpp.getRoomPromotionId().equals(promotionId))
                .collect(Collectors.toList());
            
            // 傳遞現有的價格設定
            model.addAttribute("existingPriceSettings", existingPriceSettings);
            
            // 獲取所有房型
            List<RoomType> allRoomTypes = roomTypeService.findAll();
            
            // 獲取已設定價格的房型ID列表
            List<Integer> assignedRoomTypeIds = existingPriceSettings.stream()
                .map(rpp -> rpp.getRoomTypeId())
                .collect(Collectors.toList());
            
            // 過濾出未分配的房型
            List<RoomType> unassignedRoomTypes = allRoomTypes.stream()
                .filter(roomType -> !assignedRoomTypeIds.contains(roomType.getRoomTypeId()))
                .collect(Collectors.toList());
            
            model.addAttribute("roomTypeListData", allRoomTypes);
            model.addAttribute("unassignedRoomTypeList", unassignedRoomTypes);
            
            return "back-end/roompromotionprice/update_roompromotionprice_input";
        }
        
        // 設定複合主鍵
        RoomPromotionPriceId id = new RoomPromotionPriceId();
        id.setRoomPromotionId(roomPromotionPrice.getPromotion().getRoomPromotionId());
        id.setRoomTypeId(roomPromotionPrice.getRoomType().getRoomTypeId());
        roomPromotionPrice.setId(id);
        
        roomPromotionPriceSvc.updateRoomPromotionPrice(roomPromotionPrice);
        return "redirect:/backend/roompromotionprice/listAll";
    }

    @GetMapping("/listAll")
    public String listAllRoomPromotionPrices(ModelMap model) {
       
        model.addAttribute("roomPromotionPriceListData", roomPromotionPriceSvc.getDistinctPromotions());
        model.addAttribute("promotionRoomTypeCounts", roomPromotionPriceSvc.getPromotionRoomTypeCounts());
        model.addAttribute("sidebarActive", "roompromotionprice-list");
        return "back-end/roompromotionprice/listAllRoomPromotionPrice";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("roomPromotionId") Integer id, ModelMap model) {
//        roomPromotionPriceSvc.deleteRoomPromotionPrice(id);
        return "redirect:/backend/roompromotionprice/listAll";
    }

    @PostMapping("/checkDuplicate")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(@RequestParam("roomPromotionId") Integer roomPromotionId, @RequestParam("roomTypeId") Integer roomTypeId) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isDuplicate = roomPromotionPriceSvc.isDuplicate(roomPromotionId, roomTypeId);
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addPriceSetting")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addPriceSetting(@RequestParam("roomPromotionId") Integer roomPromotionId, 
                                                              @RequestParam("roomTypeId") Integer roomTypeId,
                                                              @RequestParam("roomDiscountRate") Double roomDiscountRate) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 檢查是否重複
            if (roomPromotionPriceSvc.isDuplicate(roomPromotionId, roomTypeId)) {
                response.put("success", false);
                response.put("message", "該房型已設定過價格，請選擇其他房型");
                return ResponseEntity.ok(response);
            }
            
            // 創建新的價格設定
            RoomPromotionPrice newPriceSetting = new RoomPromotionPrice();
            RoomPromotionPriceId id = new RoomPromotionPriceId();
            id.setRoomPromotionId(roomPromotionId);
            id.setRoomTypeId(roomTypeId);
            newPriceSetting.setId(id);
            newPriceSetting.setRoomDiscountRate(roomDiscountRate);
            
            // 設定優惠專案和房型關聯
            Promotion promotion = promotionService.getOnePromotion(roomPromotionId);
            RoomType roomType = roomTypeService.findById(roomTypeId);
            newPriceSetting.setPromotion(promotion);
            newPriceSetting.setRoomType(roomType);
            
            // 設定日期（使用優惠專案的日期）
//            newPriceSetting.setPromotionStartDate(promotion.getPromotionStartDate().toLocalDate());
//            newPriceSetting.setPromotionEndDate(promotion.getPromotionEndDate().toLocalDate());
            
            // 儲存到資料庫
            roomPromotionPriceSvc.addRoomPromotionPrice(newPriceSetting);
            
            response.put("success", true);
            response.put("message", "價格設定新增成功");
            response.put("roomTypeName", roomType.getRoomTypeName());
            response.put("roomDiscountRate", roomDiscountRate);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "新增失敗：" + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deleteOne")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteOne(@RequestParam("roomPromotionId") Integer roomPromotionId, 
                                                        @RequestParam("roomTypeId") Integer roomTypeId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 創建複合主鍵
            RoomPromotionPriceId id = new RoomPromotionPriceId();
            id.setRoomPromotionId(roomPromotionId);
            id.setRoomTypeId(roomTypeId);
            
            // 刪除資料
            roomPromotionPriceSvc.deleteRoomPromotionPrice(id);
            
            response.put("success", true);
            response.put("message", "價格設定刪除成功");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除失敗：" + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saveAll")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveAll(@RequestParam("roomPromotionId") Integer roomPromotionId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 這裡可以加入任何需要的最終處理邏輯
            // 目前只是簡單的成功回應，因為資料已經在新增時儲存了
            
            response.put("success", true);
            response.put("message", "所有資料儲存成功");
            response.put("redirectUrl", "/backend/roompromotionprice/listAll");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "儲存失敗：" + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateDiscountRate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateDiscountRate(@RequestParam("roomPromotionId") Integer roomPromotionId, 
                                                                 @RequestParam("roomTypeId") Integer roomTypeId,
                                                                 @RequestParam("roomDiscountRate") Double roomDiscountRate) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 創建複合主鍵
            RoomPromotionPriceId id = new RoomPromotionPriceId();
            id.setRoomPromotionId(roomPromotionId);
            id.setRoomTypeId(roomTypeId);
            
            // 獲取現有的價格設定
            RoomPromotionPrice existingPrice = roomPromotionPriceSvc.getOneRoomPromotionPrice(id);
            if (existingPrice == null) {
                response.put("success", false);
                response.put("message", "找不到指定的價格設定");
                return ResponseEntity.ok(response);
            }
            
            // 更新折扣率
            existingPrice.setRoomDiscountRate(roomDiscountRate);
            roomPromotionPriceSvc.updateRoomPromotionPrice(existingPrice);
            
            response.put("success", true);
            response.put("message", "折扣率更新成功");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失敗：" + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
