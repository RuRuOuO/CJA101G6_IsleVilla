package com.islevilla.wei.room.controller;

import com.islevilla.lai.members.model.Members;
import com.islevilla.wei.room.model.RoomRVDetail;
import com.islevilla.wei.room.model.RoomRVDetailService;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RoomRVController {
    @Autowired
    private RoomRVOrderService roomRVOrderService;
    @Autowired
    private RoomRVDetailService roomRVDetailService;

    // 前台渲染會員訂單
    @GetMapping("/member/room/list")
    public String getRoomRVOrdersFromMember(Model model, HttpSession session) {
        Members loginMember = (Members) session.getAttribute("member");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        List<RoomRVOrder> orderList = roomRVOrderService.getRoomRVOrderByMemberDesc(loginMember);
        model.addAttribute("orderList", orderList);

        Map<Integer, List<RoomRVDetail>> detailMap = new HashMap<>();
        Map<Integer, Integer> refundAmountMap = new HashMap<>();
        Map<Integer, Integer> refundRateMap = new HashMap<>();
        for (RoomRVOrder order : orderList) {
            List<RoomRVDetail> details = roomRVDetailService.getDetailsByRoomRVOrderId(order.getRoomReservationId());
            detailMap.put(order.getRoomReservationId(), details);

            // 預先計算退款比例與金額
            double rate = roomRVOrderService.calculateRefundRate(order.getCheckInDate());
            int refundAmount = (int) Math.round(order.getRvPaidAmount() * rate);
            refundAmountMap.put(order.getRoomReservationId(), refundAmount);
            refundRateMap.put(order.getRoomReservationId(), (int) Math.round(rate * 100));
        }
        model.addAttribute("detailMap", detailMap);
        model.addAttribute("refundAmountMap", refundAmountMap);
        model.addAttribute("refundRateMap", refundRateMap);
        Map<Integer, Long> daysToCheckInMap = new HashMap<>();
        for (RoomRVOrder order : orderList) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), order.getCheckInDate());
            daysToCheckInMap.put(order.getRoomReservationId(), days);
        }
        model.addAttribute("daysToCheckInMap", daysToCheckInMap);

        return "front-end/member/member-room-list";
    }

    // 前台取消訂單
    @PostMapping("/member/room/{id}/cancel")
    public String cancelOrderFront(@PathVariable Integer id) {
        roomRVOrderService.cancelOrderFront(id);
        return "redirect:/member/room/list";
    }

    // 後台取消訂單
    @PostMapping("/backend/room-reservation/{id}/cancel")
    public String cancelOrderBack(@PathVariable Integer id) {
        roomRVOrderService.cancelOrderBack(id);
        return "redirect:/backend/check-in-out/list";
    }

//    // 後台顯示全部訂單 // pagable
//    @GetMapping("/backend/room-reservation/list")
//    public String roomRVOrderList(
//            // @RequestParam 從網址參數中取值，defaultValue設定預設值
//            @RequestParam(defaultValue = "0") int page, // 頁碼從0開始
//            @RequestParam(defaultValue = "10") int size, // 每頁10筆訂單
//            Model model,
//            HttpServletRequest request) {
//        // 建立分頁物件、設定頁碼、每頁筆數、排序方式
//        Pageable pageable = PageRequest.of(page, size, Sort.by("roomOrderDate").descending());
//        Page<RoomRVOrder> roomRVOrderPage = roomRVOrderService.getAll(pageable);
//        // 透過 request 取得目前 URL
//        String requestUrl = request.getRequestURI();
//        PageUtil.ModelWithPage(roomRVOrderPage, model, page, "orderList", request);
//
//        return "back-end/roomRVOrder/listAllRoomRVOrder";
//    }

    // 後台顯示全部訂單 // 使用前端分頁
    @GetMapping("/backend/room-reservation/list")
    public String roomRVOrderList(Model model) {
        List<RoomRVOrder> orderList = roomRVOrderService.getAllOrders();
        model.addAttribute("orderList", orderList);
        return "back-end/roomRVOrder/listAllRoomRVOrder";
    }

    @GetMapping("/backend/order-detail/{id}")
    public String getOrderDetail(@PathVariable Integer id, Model model) {
        RoomRVOrder order = roomRVOrderService.getById(id);
        if (order == null) {
            model.addAttribute("error", "查無此訂單");
            return "fragments/roomRV :: emptyOrder";
        }
        List<RoomRVDetail> detailList = roomRVDetailService.getDetailsByRoomRVOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("detailList", detailList);

        // 顯示退款相關資訊
        Map<Integer, Integer> refundRateMap = new HashMap<>();
        double rate = roomRVOrderService.calculateRefundRate(order.getCheckInDate());
        refundRateMap.put(order.getRoomReservationId(), (int) Math.round(rate * 100));
        model.addAttribute("refundRateMap", refundRateMap);

        return "fragments/roomRV :: roomRVOrder";
    }
}