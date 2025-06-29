package com.islevilla.wei.room.controller;

import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersRepository;
import com.islevilla.lai.members.model.MembersService;
import com.islevilla.wei.PageUtil;
import com.islevilla.wei.news.model.News;
import com.islevilla.wei.room.model.RoomRVDetail;
import com.islevilla.wei.room.model.RoomRVDetailService;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class RoomRVController {
    @Autowired
    MembersRepository membersRepository;
    @Autowired
    MembersService membersService;
    @Autowired
    private RoomRVOrderService roomRVOrderService;
    @Autowired
    private RoomRVDetailService roomRVDetailService;

    // 前台渲染會員訂單
    @GetMapping("/member/room/list")
    public String getRoomRVOrdersFromMember(Model model, HttpSession session) {
//  public String getRoomRVOrdersFromMember(Model model) {
        // // 從session抓出登入的會員物件
        Members loginMember = (Members) session.getAttribute("member");
        // 先寫死跳過登入步驟
//         Members loginMember = membersService.getOneMember(8);
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        // 查詢訂單
        List<RoomRVOrder> orderList = roomRVOrderService.getRoomRVOrderByMember(loginMember);
        if (!orderList.isEmpty()) {
            model.addAttribute("orderList", orderList);
        }
        return "front-end/member/member-room-list";
    }

    // 前台渲染訂單明細
    @GetMapping("/member/room/{id}")
    public String getRoomRVOrdersFromMember(@PathVariable Integer id, Model model) {
        // 查詢訂單
        List<RoomRVOrder> orderList = List.of(roomRVOrderService.getById(id));
        model.addAttribute("orderList", orderList);
        // 查詢訂單明細
        List<RoomRVDetail> detailList = roomRVDetailService.getDetailsByRoomRVOrderId(orderList.get(0).getRoomReservationId());
        model.addAttribute("detailList", detailList);

        return "front-end/member/member-room-detail";
    }

    // 前台取消訂單
    @PostMapping("/member/room/{id}/cancel")
    public String cancelOrderFront(@PathVariable Integer id) {
        roomRVOrderService.cancelOrderFront(id);
        return "redirect:/member/room/list";
    }

    // 後台取消訂單
    @PostMapping("/backend/room/reservation/order/{id}/cancel")
    public String cancelOrderBack(@PathVariable Integer id) {
        roomRVOrderService.cancelOrderBack(id);
        return "redirect:/backend/room/reservation/order/list";
    }

    // 後台顯示全部訂單
    @GetMapping("/backend/room/reservation/order/list")
    public String roomRVOrderList(
            // @RequestParam 從網址參數中取值，defaultValue設定預設值
            @RequestParam(defaultValue = "0") int page, // 頁碼從0開始
            @RequestParam(defaultValue = "10") int size, // 每頁10筆訂單
            Model model,
            HttpServletRequest request) {
        // 建立分頁物件、設定頁碼、每頁筆數、排序方式
        Pageable pageable = PageRequest.of(page, size, Sort.by("roomOrderDate").descending());
        Page<RoomRVOrder> roomRVOrderPage = roomRVOrderService.getAll(pageable);
        // 透過 request 取得目前 URL
        String requestUrl = request.getRequestURI();
        PageUtil.ModelWithPage(roomRVOrderPage, model, page, "orderList", request);

        return "back-end/roomRVOrder/listAllRoomRVOrder";
    }

    // 後台顯示訂單明細
    @GetMapping("/backend/room/reservation/order/{id}")
    public String getOneRoomRVOrder(@PathVariable("id") Integer id, Model model) {
        // 訂單
        RoomRVOrder orderList = roomRVOrderService.getById(id);
        model.addAttribute("orderList", orderList);
        // 訂單明細
        List<RoomRVDetail> detailList = roomRVDetailService.getDetailsByRoomRVOrderId(id);
        model.addAttribute("detailList", detailList);

        return "back-end/roomRVOrder/listOneRoomRVOrder";
    }

}