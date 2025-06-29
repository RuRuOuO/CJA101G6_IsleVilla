package com.islevilla.jay.dashboard.controller;

import com.islevilla.jay.dashboard.model.DashboardDTO;
import com.islevilla.jay.dashboard.model.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DashBoardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/backend/dashboard")
    public String dashboard(@RequestParam(value = "tab", required = false, defaultValue = "confirmed") String tab, Model model) {
        // 透過Service取得dashboard資料
        DashboardDTO dashboardData = dashboardService.getTodayDashboardData();
        
        // 將資料加入Model
        model.addAttribute("todayProductOrders", dashboardData.getTodayProductOrders());
        // model.addAttribute("todayNewMembers", dashboardData.getTodayNewMembers());
        model.addAttribute("todayProductRevenue", dashboardData.getTodayProductRevenue());
        model.addAttribute("pendingProductOrders", dashboardData.getPendingProductOrders());
        model.addAttribute("confirmedProductOrders", dashboardData.getConfirmedProductOrders());
        
        // 新增房間訂單相關資料
        model.addAttribute("todayRoomOrders", dashboardData.getTodayRoomOrders());
        model.addAttribute("todayRoomRevenue", dashboardData.getTodayRoomRevenue());
        model.addAttribute("todayRoomOrdersList", dashboardData.getTodayRoomOrdersList());
        model.addAttribute("roomOccupancyRate", dashboardData.getRoomOccupancyRate());

        // 新增今日所有訂單
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(23, 59, 59);
        List<com.islevilla.jay.productOrder.model.ProductOrder> todayProductOrderList = dashboardService.findTodayAllProductOrders(todayStart, todayEnd);
        model.addAttribute("todayProductOrderList", todayProductOrderList);

        model.addAttribute("tab", tab);
        return "back-end/dashboard";
    }
}
