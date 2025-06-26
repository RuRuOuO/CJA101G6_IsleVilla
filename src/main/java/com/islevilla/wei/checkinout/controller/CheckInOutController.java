package com.islevilla.wei.checkinout.controller;

import com.islevilla.wei.checkinout.service.CheckInOutService;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CheckInOutController {
    @Autowired
    private CheckInOutService checkInOutService;
    @Autowired
    private RoomRVOrderService roomRVOrderService;

    // 後台checkIn頁面
    @GetMapping("/backend/checkInOut/dashboard")
    public String checkInOutDashboard(Model model) {
        List<RoomRVOrder> orderList = roomRVOrderService.findAll();
        if (!orderList.isEmpty()) {
            model.addAttribute("orderList", orderList);
        }
        return "back-end/check-in-out/dashboard";
    }
}
