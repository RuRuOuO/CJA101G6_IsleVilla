package com.islevilla.chen.roomTypeAvailability.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailability;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityId;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityService;
import com.islevilla.chen.util.exception.BusinessException;
import com.islevilla.chen.util.map.RoomTypeName;

@Controller
@RequestMapping("/backend/roomTypeAvailability")
public class RoomTypeAvailabilityController {

    @Autowired
    private RoomTypeAvailabilityService roomTypeAvailabilityService;
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    @Autowired
    private RoomTypeName roomTypeName;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    

	    //準備頁面顯示所需的資料
    private void prepareModelForView(Model model) {
        // 使用 RoomTypeName 工具類獲取房型資料
        List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
        Map<Integer, String> roomTypeMap = roomTypeName.getRoomTypeNameMap();
        
        model.addAttribute("roomTypeList", roomTypeList);
        model.addAttribute("roomTypeMap", roomTypeMap);
        model.addAttribute("roomTypeName", roomTypeMap); // 保持與其他 Controller 一致的命名
    }
    
    // 準備交叉表格所需的數據結構 - 使用實時庫存計算
    private void prepareMatrixData(List<RoomTypeAvailability> availabilityList, Model model) {
        // 1. 獲取所有房型
        List<RoomType> roomTypeList = roomTypeName.getRoomTypeNameList();
        
        // 2. 從 model 中獲取當前查詢的日期範圍
        LocalDate startDate = (LocalDate) model.getAttribute("selectedStartDate");
        LocalDate endDate = (LocalDate) model.getAttribute("selectedEndDate");
        
        // 3. 生成該月份的所有日期
        List<LocalDate> dateList = new java.util.ArrayList<>();
        if (startDate != null && endDate != null) {
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                dateList.add(current);
                current = current.plusDays(1);
            }
        } else {
            // 備用方案：如果沒有日期範圍，使用當前月份
            LocalDate today = LocalDate.now();
            LocalDate monthStart = today.withDayOfMonth(1);
            LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
            
            LocalDate current = monthStart;
            while (!current.isAfter(monthEnd)) {
                dateList.add(current);
                current = current.plusDays(1);
            }
        }
        
        // 3. 建立實時庫存矩陣：房型ID -> (日期 -> 實際可用數量)
        Map<Integer, Map<LocalDate, Integer>> matrix = new HashMap<>();
        Map<Integer, Map<LocalDate, Integer>> staticMatrix = new HashMap<>(); // 靜態設定的數量
        
        // 先處理靜態設定的數量
        for (RoomTypeAvailability availability : availabilityList) {
            Integer roomTypeId = availability.getRoomTypeAvailabilityId().getRoomTypeId();
            LocalDate date = availability.getRoomTypeAvailabilityId().getRoomTypeAvailabilityDate();
            Integer count = availability.getRoomTypeAvailabilityCount();
            
            // 確保不會存入 null 值
            staticMatrix.computeIfAbsent(roomTypeId, k -> new HashMap<>()).put(date, count != null ? count : 0);
        }
        
        // 計算每個房型在每個日期的實時可用數量
        for (RoomType roomType : roomTypeList) {
            Integer roomTypeId = roomType.getRoomTypeId();
            Map<LocalDate, Integer> roomTypeMatrix = new HashMap<>();
            
            for (LocalDate date : dateList) {
                try {
                    // 使用 service 中的方法計算實時可用數量
                    Integer realTimeAvailable = roomTypeAvailabilityService.calculateAvailableRooms(roomTypeId, date);
                    // 確保不會存入 null 值
                    roomTypeMatrix.put(date, realTimeAvailable != null ? realTimeAvailable : 0);
                } catch (Exception e) {
                    // 如果計算失敗，使用房型總數量作為備用
                    Integer fallbackValue = roomType.getRoomTypeQuantity() != null ? roomType.getRoomTypeQuantity() : 0;
                    roomTypeMatrix.put(date, fallbackValue);
                    System.err.println("計算房型 " + roomTypeId + " 在 " + date + " 的庫存時發生錯誤: " + e.getMessage());
                }
            }
            
            matrix.put(roomTypeId, roomTypeMatrix);
        }
        
        // 4. 將數據添加到 Model
        model.addAttribute("dateList", dateList);
        model.addAttribute("matrix", matrix); // 實時計算的庫存
        model.addAttribute("staticMatrix", staticMatrix); // 靜態設定的庫存（用於對比）
        
        System.out.println("準備實時庫存數據 - 日期數: " + dateList.size() + ", 房型數: " + matrix.size());
        System.out.println("日期範圍: " + (dateList.isEmpty() ? "無" : dateList.get(0) + " 至 " + dateList.get(dateList.size()-1)));
    }
    

    //顯示房型可用性列表頁面 (支援複合查詢，DataTable處理分頁)
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('room')")
    public String showRoomTypeAvailabilityList(
            @RequestParam(value = "roomTypeId", required = false) Integer roomTypeId,
            @RequestParam(value = "selectedMonth", required = false) String selectedMonth,
            Model model) {
        
    	System.out.println("進入房型可用性列表頁面 - 月份視圖");
        
        // 處理月份參數，如果沒有指定則使用當前月份
        LocalDate targetMonth;
        if (selectedMonth != null && !selectedMonth.isEmpty()) {
            try {
                // selectedMonth 格式為 "yyyy-MM"
                YearMonth yearMonth = YearMonth.parse(selectedMonth);
                targetMonth = yearMonth.atDay(1);
            } catch (Exception e) {
                targetMonth = LocalDate.now().withDayOfMonth(1);
            }
        } else {
            targetMonth = LocalDate.now().withDayOfMonth(1);
        }
        
        // 計算該月份的開始和結束日期
        LocalDate startDate = targetMonth;
        LocalDate endDate = targetMonth.withDayOfMonth(targetMonth.lengthOfMonth());
        
        System.out.println("查詢月份: " + targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        System.out.println("日期範圍: " + startDate + " 到 " + endDate);
        
    	List<RoomTypeAvailability> availabilityList;
        
        try {
        	// 查詢該月份的所有庫存記錄
            availabilityList = roomTypeAvailabilityService.findByComplexQueryAll(
            		roomTypeId, null, startDate, endDate);
            
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            availabilityList = new java.util.ArrayList<>();
        }
        
        // 準備頁面資料
        prepareModelForView(model);
        
        // 如果 Model 中沒有 roomTypeAvailability 物件，就新增一個空的
        if (!model.containsAttribute("roomTypeAvailability")) {
            model.addAttribute("roomTypeAvailability", new RoomTypeAvailability());
        }
        
        // 如果 Model 中沒有 batchCreate 物件，就新增一個空的
        if (!model.containsAttribute("batchCreate")) {
            Map<String, Object> batchCreate = new HashMap<>();
            batchCreate.put("roomTypeId", null);
            batchCreate.put("startDate", null);
            batchCreate.put("endDate", null);
            batchCreate.put("defaultCount", 0);
            model.addAttribute("batchCreate", batchCreate);
        }
        
        // 資料列表
        model.addAttribute("availabilityList", availabilityList);
        
        // 準備交叉表格所需的資料結構
        prepareMatrixData(availabilityList, model);
        
        // 查詢條件
        model.addAttribute("selectedRoomTypeId", roomTypeId);
        model.addAttribute("selectedMonth", selectedMonth != null ? selectedMonth : targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);
        

        
        System.out.println("查詢結果 - 總記錄數: " + availabilityList.size());
        
        return "back-end/roomTypeAvailability/listRoomTypeAvailability";
    }
    
    /**
     * 新增房型可用性記錄
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('room')")
    public String addRoomTypeAvailability(
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("availabilityDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate availabilityDate,
            @RequestParam("availabilityCount") Integer availabilityCount,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("處理新增房型可用性請求");
        System.out.println("房型ID: " + roomTypeId + ", 日期: " + availabilityDate + ", 數量: " + availabilityCount);
        
        try {
            // 建立複合主鍵
            RoomTypeAvailabilityId id = new RoomTypeAvailabilityId(roomTypeId, availabilityDate);
            
            // 建立房型可用性物件
            RoomTypeAvailability availability = new RoomTypeAvailability();
            availability.setRoomTypeAvailabilityId(id);
            availability.setRoomTypeAvailabilityCount(availabilityCount);
            
            roomTypeAvailabilityService.addRoomTypeAvailability(availability);
            redirectAttributes.addFlashAttribute("successMessage", "房型可用性新增成功！");
            
        } catch (BusinessException e) {
            model.addAttribute("addErrorMessage", List.of(e.getMessage()));
            prepareModelForView(model);
            
            // 重新載入資料
            List<RoomTypeAvailability> availabilityList = roomTypeAvailabilityService.findAll();
            model.addAttribute("availabilityList", availabilityList);
            model.addAttribute("roomTypeAvailability", new RoomTypeAvailability());
            
            return "back-end/roomTypeAvailability/listRoomTypeAvailability";
        }
        
        return "redirect:/backend/roomTypeAvailability/list";
    }
    
    /**
     * 更新房型可用性記錄
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('room')")
    public String updateRoomTypeAvailability(
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("availabilityDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate availabilityDate,
            @RequestParam("availabilityCount") Integer availabilityCount,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("處理更新房型可用性請求");
        System.out.println("房型ID: " + roomTypeId + ", 日期: " + availabilityDate + ", 數量: " + availabilityCount);
        
        try {
            // 建立複合主鍵
            RoomTypeAvailabilityId id = new RoomTypeAvailabilityId(roomTypeId, availabilityDate);
            
            // 建立房型可用性物件
            RoomTypeAvailability availability = new RoomTypeAvailability();
            availability.setRoomTypeAvailabilityId(id);
            availability.setRoomTypeAvailabilityCount(availabilityCount);
            
            roomTypeAvailabilityService.updateRoomTypeAvailability(availability);
            redirectAttributes.addFlashAttribute("successMessage", "房型可用性更新成功！");
            
        } catch (BusinessException e) {
            model.addAttribute("updateErrorMessage", List.of(e.getMessage()));
            prepareModelForView(model);
            
            // 重新載入資料
            List<RoomTypeAvailability> availabilityList = roomTypeAvailabilityService.findAll();
            model.addAttribute("availabilityList", availabilityList);
            model.addAttribute("roomTypeAvailability", new RoomTypeAvailability());
            
            return "back-end/roomTypeAvailability/listRoomTypeAvailability";
        }
        
        return "redirect:/backend/roomTypeAvailability/list";
    }
    
    /**
     * 刪除房型可用性記錄
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('room')")
    public String deleteRoomTypeAvailability(
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("availabilityDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate availabilityDate,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("處理刪除房型可用性請求");
        System.out.println("房型ID: " + roomTypeId + ", 日期: " + availabilityDate);
        
        try {
            roomTypeAvailabilityService.deleteRoomTypeAvailability(roomTypeId, availabilityDate);
            redirectAttributes.addFlashAttribute("successMessage", "房型可用性刪除成功！");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/backend/roomTypeAvailability/list";
    }
    
    /**
     * 批量創建房型可用性記錄
     */
    @PostMapping("/batchCreate")
    @PreAuthorize("hasAuthority('room')")
    public String batchCreateRoomTypeAvailability(
            @RequestParam("batchRoomTypeId") Integer roomTypeId,
            @RequestParam("batchStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("batchEndDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam("batchDefaultCount") Integer defaultCount,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("處理批量創建房型可用性請求");
        System.out.println("房型ID: " + roomTypeId + ", 開始日期: " + startDate + ", 結束日期: " + endDate + ", 預設數量: " + defaultCount);
        
        try {
            List<RoomTypeAvailability> createdList = roomTypeAvailabilityService.batchCreateAvailability(
                roomTypeId, startDate, endDate, defaultCount);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "批量創建成功！共創建了 " + createdList.size() + " 筆房型可用性記錄");
            
        } catch (BusinessException e) {
            model.addAttribute("batchErrorMessage", List.of(e.getMessage()));
            prepareModelForView(model);
            
            // 重新載入資料
            List<RoomTypeAvailability> availabilityList = roomTypeAvailabilityService.findAll();
            model.addAttribute("availabilityList", availabilityList);
            model.addAttribute("roomTypeAvailability", new RoomTypeAvailability());
            
            // 保持批量創建的表單資料
            Map<String, Object> batchCreate = new HashMap<>();
            batchCreate.put("roomTypeId", roomTypeId);
            batchCreate.put("startDate", startDate);
            batchCreate.put("endDate", endDate);
            batchCreate.put("defaultCount", defaultCount);
            model.addAttribute("batchCreate", batchCreate);
            
            return "back-end/roomTypeAvailability/listRoomTypeAvailability";
        }
        
        return "redirect:/backend/roomTypeAvailability/list";
    }
    
    /**
     * 檢查特定房型和日期的可用性是否存在（AJAX 用）
     */
    @GetMapping("/checkExists")
    @PreAuthorize("hasAuthority('room')")
    public String checkAvailabilityExists(
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("availabilityDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate availabilityDate,
            Model model) {
        
        boolean exists = roomTypeAvailabilityService.existsAvailability(roomTypeId, availabilityDate);
        model.addAttribute("exists", exists);
        model.addAttribute("roomTypeId", roomTypeId);
        model.addAttribute("availabilityDate", availabilityDate);
        
        return "fragments/availabilityCheck";
    }
    
    /**
     * 顯示房型可用性詳細資訊
     */
    @GetMapping("/detail")
    @PreAuthorize("hasAuthority('room')")
    public String showRoomTypeAvailabilityDetail(
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("availabilityDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate availabilityDate,
            Model model) {
        
        try {
            RoomTypeAvailability availability = roomTypeAvailabilityService.findById(roomTypeId, availabilityDate);
            
            if (availability == null) {
                model.addAttribute("errorMessage", "查無此房型可用性記錄");
                return "redirect:/backend/roomTypeAvailability/list";
            }
            
            model.addAttribute("availability", availability);
            prepareModelForView(model);
            
            return "back-end/roomTypeAvailability/detailRoomTypeAvailability";
            
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/backend/roomTypeAvailability/list";
        }
    }
    
    // ==================== 庫存計算相關端點 ====================
    
    /**
     * 計算特定房型在特定日期的實際可用數量 (AJAX 用)
     */
    @GetMapping("/calculateAvailable")
    @PreAuthorize("hasAuthority('room')")
    public String calculateAvailableRooms(
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("targetDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate,
            Model model) {
        
        try {
            Integer availableCount = roomTypeAvailabilityService.calculateAvailableRooms(roomTypeId, targetDate);
            model.addAttribute("success", true);
            model.addAttribute("availableCount", availableCount);
            model.addAttribute("roomTypeId", roomTypeId);
            model.addAttribute("targetDate", targetDate);
        } catch (BusinessException e) {
            model.addAttribute("success", false);
            model.addAttribute("errorMessage", e.getMessage());
        }
        
        return "fragments/availabilityCalculation";
    }
    
    /**
     * 同步房型可用性記錄 (根據實際庫存計算)
     */
    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('room')")
    public String syncRoomTypeAvailability(
            @RequestParam("syncRoomTypeId") Integer roomTypeId,
            @RequestParam("syncTargetDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("處理同步房型可用性請求");
        System.out.println("房型ID: " + roomTypeId + ", 目標日期: " + targetDate);
        
        try {
            RoomTypeAvailability synced = roomTypeAvailabilityService.syncRoomTypeAvailability(roomTypeId, targetDate);
            redirectAttributes.addFlashAttribute("successMessage", 
                "同步成功！房型 " + roomTypeId + " 在 " + targetDate + " 的可用數量已更新為 " + synced.getRoomTypeAvailabilityCount());
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/backend/roomTypeAvailability/list";
    }
    
    /**
     * 批量同步房型可用性記錄
     */
    @PostMapping("/batchSync")
    @PreAuthorize("hasAuthority('room')")
    public String batchSyncAvailability(
            @RequestParam("batchSyncRoomTypeId") Integer roomTypeId,
            @RequestParam("batchSyncStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("batchSyncEndDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("處理批量同步房型可用性請求");
        System.out.println("房型ID: " + roomTypeId + ", 開始日期: " + startDate + ", 結束日期: " + endDate);
        
        try {
            List<RoomTypeAvailability> syncedList = roomTypeAvailabilityService.batchSyncAvailability(
                roomTypeId, startDate, endDate);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "批量同步成功！房型 " + roomTypeId + " 從 " + startDate + " 到 " + endDate + 
                " 共同步了 " + syncedList.size() + " 筆記錄");
            
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/backend/roomTypeAvailability/list";
    }
    
    /**
     * 檢查庫存是否足夠 (AJAX 用)
     */
    @GetMapping("/checkStock")
    @PreAuthorize("hasAuthority('room')")
    public String checkStock(
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("checkDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkDate,
            @RequestParam("requiredCount") Integer requiredCount,
            Model model) {
        
        try {
            boolean hasEnough = roomTypeAvailabilityService.hasAvailableRooms(roomTypeId, checkDate, requiredCount);
            Integer availableCount = roomTypeAvailabilityService.calculateAvailableRooms(roomTypeId, checkDate);
            
            model.addAttribute("success", true);
            model.addAttribute("hasEnough", hasEnough);
            model.addAttribute("availableCount", availableCount);
            model.addAttribute("requiredCount", requiredCount);
            model.addAttribute("roomTypeId", roomTypeId);
            model.addAttribute("checkDate", checkDate);
        } catch (BusinessException e) {
            model.addAttribute("success", false);
            model.addAttribute("errorMessage", e.getMessage());
        }
        
        return "fragments/stockCheck";
    }
    
    /**
     * 檢查日期範圍內的庫存是否足夠 (AJAX 用)
     */
    @GetMapping("/checkStockRange")
    @PreAuthorize("hasAuthority('room')")
    public String checkStockRange(
            @RequestParam("roomTypeId") Integer roomTypeId,
            @RequestParam("checkStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("checkEndDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam("requiredCount") Integer requiredCount,
            Model model) {
        
        try {
            boolean hasEnough = roomTypeAvailabilityService.hasAvailableRoomsInRange(
                roomTypeId, startDate, endDate, requiredCount);
            
            model.addAttribute("success", true);
            model.addAttribute("hasEnough", hasEnough);
            model.addAttribute("requiredCount", requiredCount);
            model.addAttribute("roomTypeId", roomTypeId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        } catch (BusinessException e) {
            model.addAttribute("success", false);
            model.addAttribute("errorMessage", e.getMessage());
        }
        
        return "fragments/stockRangeCheck";
    }
    
    /**
     * 顯示所有房型在特定日期的庫存概況
     */
    @GetMapping("/stockOverview")
    @PreAuthorize("hasAuthority('room')")
    public String showStockOverview(
            @RequestParam("overviewDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate overviewDate,
            Model model) {
        
        try {
            Map<Integer, Integer> availabilityMap = roomTypeAvailabilityService.calculateAllRoomTypesAvailability(overviewDate);
            
            prepareModelForView(model);
            model.addAttribute("availabilityMap", availabilityMap);
            model.addAttribute("overviewDate", overviewDate);
            
            return "back-end/roomTypeAvailability/stockOverview";
            
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/backend/roomTypeAvailability/list";
        }
    }
}
