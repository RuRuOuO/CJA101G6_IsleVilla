package com.islevilla.wei.checkinout.controller;

import com.islevilla.wei.checkinout.service.CheckInOutService;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CheckInOutController {
    @Autowired
    private CheckInOutService checkInOutService;
    @Autowired
    private RoomRVOrderService roomRVOrderService;

    // 後台checkIn頁面
    @GetMapping("/backend/check-in-out/list")
    public String checkInOutDashboard(Model model) {
        List<RoomRVOrder> orderList = roomRVOrderService.getOperableOrders();
        if (!orderList.isEmpty()) {
            model.addAttribute("orderList", orderList);
        }
        return "back-end/check-in-out/check-in-out-list";
    }

    // 入住
    @PostMapping("/backend/room-reservation/{orderId}/checkin")
    public String checkIn(@PathVariable Integer orderId, RedirectAttributes redirectAttributes) {
        try {
            boolean success = checkInOutService.checkIn(orderId);
            if (success) {
                redirectAttributes.addFlashAttribute("message", "訂單 #" + orderId + " 辦理入住成功");
            } else {
                redirectAttributes.addFlashAttribute("error", "訂單 #" + orderId + " 辦理入住失敗");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "辦理入住失敗: " + e.getMessage());
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
}