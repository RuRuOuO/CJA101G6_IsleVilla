package com.islevilla.chen.roomTypeAvailability.controller;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeService;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailability;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityId;
import com.islevilla.chen.roomTypeAvailability.model.RoomTypeAvailabilityService;
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

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('room')")
    public String showList(
            @RequestParam(value = "roomTypeId", defaultValue = "0") Integer roomTypeId,
            @RequestParam(value = "yearMonth", required = false) String yearMonthStr,
            Model model) {

        Integer year;
        Integer month;

        // 如果提供了 yearMonth 參數，則解析它
        if (yearMonthStr != null && !yearMonthStr.isEmpty()) {
            YearMonth parsedYearMonth = YearMonth.parse(yearMonthStr);
            year = parsedYearMonth.getYear();
            month = parsedYearMonth.getMonthValue();
        } else {
            // 沒有提供年月就用當前年月
            YearMonth currentYearMonth = YearMonth.now();
            year = currentYearMonth.getYear();
            month = currentYearMonth.getMonthValue();
        }

        YearMonth yearMonthObject = YearMonth.of(year, month);
        List<RoomTypeAvailability> availabilityList;
        List<RoomType> roomTypes;

        try {
            if (roomTypeId == 0) {
                // 查詢所有房型庫存
                availabilityList = roomTypeAvailabilityService.findAllByYearMonth(year, month);
                roomTypes = roomTypeName.getRoomTypeNameList();
            } else {
                // 查詢單一房型庫存
                availabilityList = roomTypeAvailabilityService.findByRoomTypeIdAndYearMonth(roomTypeId, year, month);
                roomTypes = List.of(roomTypeService.findById(roomTypeId));
            }
        } catch (Exception e) {
            availabilityList = List.of();
            roomTypes = List.of();
            System.out.println("查詢失敗: " + e.getMessage());
        }

        // 將列表轉換為便於前端渲染的矩陣結構
        Map<Integer, Map<Integer, Integer>> availabilityMatrix = transformToMatrix(availabilityList);

        // ======================= DEBUGGING LOGS =======================
        System.out.println("---------- DEBUGGING START ----------");
        System.out.println("Raw availabilityList size from service: " + availabilityList.size());
        // 打印幾條原始數據，檢查 availableQuantity 是否有值
        availabilityList.stream().limit(5).forEach(item -> 
            System.out.println("Raw Item: RoomTypeID=" + item.getRoomTypeAvailabilityId().getRoomTypeId() + 
                               ", Date=" + item.getRoomTypeAvailabilityId().getRoomTypeAvailabilityDate() + 
                               ", Quantity=" + item.getRoomTypeAvailabilityCount()));
        
        System.out.println("Transformed availabilityMatrix: " + availabilityMatrix);
        System.out.println("----------- DEBUGGING END -----------");
        // ============================================================

        // 獲取當月所有天數
        List<Integer> daysInMonth = yearMonthObject.lengthOfMonth() > 0 ?
                java.util.stream.IntStream.rangeClosed(1, yearMonthObject.lengthOfMonth())
                        .boxed().collect(java.util.stream.Collectors.toList()) : List.of();

        System.out.println("進入頁面 - 年月: " + year + "-" + month + ", 房型ID: " + roomTypeId);

        model.addAttribute("selectedRoomTypeId", roomTypeId);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("roomTypes", roomTypes); // 房型列表
        model.addAttribute("availabilityMatrix", availabilityMatrix); // 庫存矩陣
        model.addAttribute("daysInMonth", daysInMonth); // 當月天數
        model.addAttribute("roomTypeNameList", roomTypeName.getRoomTypeNameList());
        model.addAttribute("roomTypeName", roomTypeName.getRoomTypeNameMap());
        return "back-end/roomTypeAvailability/listRoomTypeAvailability";
    }

    /**
     * 將庫存列表轉成 Map<房型ID, Map<日期, 可用數量>>
     */
    private Map<Integer, Map<Integer, Integer>> transformToMatrix(List<RoomTypeAvailability> availabilityList) {
        Map<Integer, Map<Integer, Integer>> matrix = new HashMap<>();
        for (RoomTypeAvailability rta : availabilityList) {
            RoomTypeAvailabilityId id = rta.getRoomTypeAvailabilityId();
            int roomTypeId = id.getRoomTypeId();
            int dayOfMonth = id.getRoomTypeAvailabilityDate().getDayOfMonth();

            //對不存在的 key 時自動建立對應物件
            matrix.computeIfAbsent(roomTypeId, k -> new HashMap<Integer, Integer>()); //Map<幾號, 數量>
            matrix.get(roomTypeId).put(dayOfMonth, rta.getRoomTypeAvailabilityCount());
        }
        return matrix;
    }
}
