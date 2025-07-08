package com.islevilla.wei.checkinout.controller;

import com.islevilla.chen.room.model.Room;
import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.wei.checkinout.service.CheckInOutService;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CheckInOutController {
    @Autowired
    private CheckInOutService checkInOutService;
    @Autowired
    private RoomRVOrderService roomRVOrderService;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    // 後台checkIn頁面
    @GetMapping("/backend/check-in-out/list")
    public String checkInOutDashboard(Model model) {
        List<RoomRVOrder> orderList = roomRVOrderService.getAllOrders();
        List<RoomRVOrder> orderListToday = roomRVOrderService.getTodayCheckInOrOutOrders();

        // 保證即使沒有資料也要傳空list，避免前端判斷null
        model.addAttribute("orderList", orderList != null ? orderList : List.of());
        model.addAttribute("orderListToday", orderListToday != null ? orderListToday : List.of());

        // === 新增：退款金額、比例、距離入住日的 map ===
        Map<Integer, Integer> refundAmountMap = new HashMap<>();
        Map<Integer, Integer> refundRateMap = new HashMap<>();
        Map<Integer, Long> daysToCheckInMap = new HashMap<>();

        // 合併兩個list，避免有訂單只在其中一個list
        List<RoomRVOrder> allOrders = new java.util.ArrayList<>();
        if (orderList != null) allOrders.addAll(orderList);
        if (orderListToday != null) {
            for (RoomRVOrder o : orderListToday) {
                if (allOrders.stream().noneMatch(x -> x.getRoomReservationId().equals(o.getRoomReservationId()))) {
                    allOrders.add(o);
                }
            }
        }

        for (RoomRVOrder order : allOrders) {
            Integer orderId = order.getRoomReservationId();
            Integer refundAmount = order.getRvRefundAmount() != null ? order.getRvRefundAmount() : 0;
            Integer paidAmount = order.getRvPaidAmount() != null ? order.getRvPaidAmount() : 1; // 避免除以0
            int refundRate = paidAmount > 0 ? (int) Math.round(refundAmount * 100.0 / paidAmount) : 0;
            refundAmountMap.put(orderId, refundAmount);
            refundRateMap.put(orderId, refundRate);

            long days = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), order.getCheckInDate());
            daysToCheckInMap.put(orderId, days);
        }

        model.addAttribute("refundAmountMap", refundAmountMap);
        model.addAttribute("refundRateMap", refundRateMap);
        model.addAttribute("daysToCheckInMap", daysToCheckInMap);

        model.addAttribute("sidebarActive", "check-in-out-list");
        return "back-end/check-in-out/check-in-out-list";
    }

    @GetMapping("/backend/room-reservation/{orderId}/select-rooms")
    public String showRoomSelection(@PathVariable Integer orderId, Model model) {
        try {
            RoomRVOrder order = roomRVOrderService.getById(orderId);
            if (order == null) {
                model.addAttribute("error", "訂單不存在");
                return "redirect:/backend/check-in-out/list";
            }
            // 檢查是否可以入住
            if (!checkInOutService.canCheckIn(orderId)) {
                model.addAttribute("error", "訂單狀態不允許入住");
                return "redirect:/backend/check-in-out/list";
            }
            // 獲取可用房間列表
            Map<Integer, List<Room>> availableRooms = checkInOutService.getAvailableRoomsForCheckIn(orderId);
            model.addAttribute("order", order);
            model.addAttribute("availableRooms", availableRooms);

            // 新增：查詢所有房型，組成 Map<房型ID, 房型名稱>
            Map<Integer, String> roomTypeNameMap = roomTypeRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(RoomType::getRoomTypeId, RoomType::getRoomTypeName));
            model.addAttribute("roomTypeNameMap", roomTypeNameMap); // 新增
            return "back-end/check-in-out/room-selection";
        } catch (Exception e) {
            model.addAttribute("error", "獲取房間資訊失敗" + e.getMessage());
            return "redirect:/backend/check-in-out/list";
        }
    }

    // 入住+選擇房間
    @PostMapping("/backend/room-reservation/{orderId}/checkin-with-rooms")
    public String checkInWithRooms(@PathVariable Integer orderId, @RequestParam Map<String, String> roomSelections, RedirectAttributes redirectAttributes) {
        try {
            // 將 roomSelection_xx 轉換成 Map<detailId, roomId>
            Map<Integer, Integer> detailRoomMap = new HashMap<>();
            for (Map.Entry<String, String> entry : roomSelections.entrySet()) {
                if (entry.getKey().startsWith("roomSelection_")) {
                    Integer detailId = Integer.parseInt(entry.getKey().replace("roomSelection_", ""));
                    Integer roomId = Integer.parseInt(entry.getValue());
                    detailRoomMap.put(detailId, roomId);
                }
            }
            boolean success = checkInOutService.checkInWithRoomSelection(orderId, detailRoomMap);
            if (success) {
                redirectAttributes.addFlashAttribute("message", "訂單 #" + orderId + "辦理入住成功");
            } else {
                redirectAttributes.addFlashAttribute("error", "訂單 #" + orderId + "辦理入住失敗");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "辦理入住失敗" + e.getMessage());
        }
        return "redirect:/backend/check-in-out/list";
    }

    // 退房
    @PostMapping("/backend/room-reservation/{orderId}/checkout")
    public String checkOut(@PathVariable Integer orderId, RedirectAttributes redirectAttributes) {
        try {
            boolean success = checkInOutService.checkOut(orderId);
            if (success) {
                redirectAttributes.addFlashAttribute("message", "訂單 #" + orderId + " 辦理退房成功");
            } else {
                redirectAttributes.addFlashAttribute("error", "訂單 #" + orderId + " 辦理退房失敗");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "辦理退房失敗: " + e.getMessage());
        }
        return "redirect:/backend/check-in-out/list";
    }

    // AJAX獲取可用房間
    @GetMapping("/backend/room-reservation/{orderId}/available-rooms")
    @ResponseBody
    public Map<Integer, List<Room>> getAvailableRooms(@PathVariable Integer orderId) {
        return checkInOutService.getAvailableRoomsForCheckIn(orderId);
    }
}