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
        Map<Integer, Long> daysToCheckInMap = new HashMap<>();

        // 新增：計算各種金額的 Map
        Map<Integer, Integer> totalAmountMap = new HashMap<>();
        Map<Integer, Integer> originalAmountMap = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> detailActualAmountMap = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> detailOriginalAmountMap = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> detailDiscountAmountMap = new HashMap<>();

        for (RoomRVOrder order : orderList) {
            Integer orderId = order.getRoomReservationId();
            List<RoomRVDetail> details = roomRVDetailService.getDetailsByRoomRVOrderId(orderId);
            detailMap.put(orderId, details);

            // 計算入住天數
            long days = ChronoUnit.DAYS.between(order.getCheckInDate(), order.getCheckOutDate());

            // 計算各明細的金額
            Map<Integer, Integer> detailActualAmounts = new HashMap<>();
            Map<Integer, Integer> detailOriginalAmounts = new HashMap<>();
            Map<Integer, Integer> detailDiscountAmounts = new HashMap<>();
            int totalAmount = 0;
            int originalAmount = 0;

            for (RoomRVDetail detail : details) {
                Integer detailId = detail.getRoomReservationDetailId();

                // 計算原始金額（未折扣）
                int detailOriginalAmount = (int) (detail.getRoomPrice() * days);
                detailOriginalAmounts.put(detailId, detailOriginalAmount);
                originalAmount += detailOriginalAmount;

                // 計算總折扣金額（折扣金額 × 天數）
                int detailDiscountAmount = (int) (detail.getRvDiscountAmount() * days);
                detailDiscountAmounts.put(detailId, detailDiscountAmount);

                // 計算實付金額（已折扣）
                int actualPrice = detail.getRoomPrice() - detail.getRvDiscountAmount();
                int detailActualAmount = (int) (actualPrice * days);
                detailActualAmounts.put(detailId, detailActualAmount);
                totalAmount += detailActualAmount;
            }

            // 存入各種金額 Map
            totalAmountMap.put(orderId, totalAmount);
            originalAmountMap.put(orderId, originalAmount);
            detailActualAmountMap.put(orderId, detailActualAmounts);
            detailOriginalAmountMap.put(orderId, detailOriginalAmounts);
            detailDiscountAmountMap.put(orderId, detailDiscountAmounts);

            // 計算退款比例與金額
            double rate = roomRVOrderService.calculateRefundRate(order.getCheckInDate());
            int refundAmount = (int) Math.round(totalAmount * rate);
            refundAmountMap.put(orderId, refundAmount);
            refundRateMap.put(orderId, (int) Math.round(rate * 100));

            // 計算距離入住日天數
            long daysToCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), order.getCheckInDate());
            daysToCheckInMap.put(orderId, daysToCheckIn);
        }

        // 將所有 Map 加入 model
        model.addAttribute("detailMap", detailMap);
        model.addAttribute("refundAmountMap", refundAmountMap);
        model.addAttribute("refundRateMap", refundRateMap);
        model.addAttribute("daysToCheckInMap", daysToCheckInMap);
        model.addAttribute("totalAmountMap", totalAmountMap);
        model.addAttribute("originalAmountMap", originalAmountMap);
        model.addAttribute("detailActualAmountMap", detailActualAmountMap);
        model.addAttribute("detailOriginalAmountMap", detailOriginalAmountMap);
        model.addAttribute("detailDiscountAmountMap", detailDiscountAmountMap);

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

        // 新增退款金額、比例、距離入住日的 map
        Map<Integer, Integer> refundAmountMap = new HashMap<>();
        Map<Integer, Integer> refundRateMap = new HashMap<>();
        Map<Integer, Long> daysToCheckInMap = new HashMap<>();

        for (RoomRVOrder order : orderList) {
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