package com.islevilla.wei.room.controller;

import com.islevilla.lai.members.model.Members;
import com.islevilla.lai.members.model.MembersRepository;
import com.islevilla.lai.members.model.MembersService;
import com.islevilla.wei.PageUtil;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Member;
import java.util.List;

@Controller
public class RoomRVController {
    @Autowired
    private RoomRVOrderService roomRVOrderService;

    @Autowired
    private RoomRVDetailService roomRVDetailService;

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
        PageUtil.ModelWithPage(roomRVOrderPage, model, page, "roomRVOrderList", request);

        return "back-end/roomRVOrder/listAllRoomRVOrder";
    }

    @GetMapping("/backend/room/reservation/order/{id}")
    public String getOneRoomRVOrder(@PathVariable("id") Integer id, Model model) {
        // 訂單
        RoomRVOrder roomRVOrder = roomRVOrderService.getById(id);
        model.addAttribute("roomRVOrder", roomRVOrder);
        // 訂單明細
        List<RoomRVDetail> details = roomRVDetailService.getDetailsByRoomRVOrderId(id);
        model.addAttribute("details", details);

        return "back-end/roomRVOrder/listOneRoomRVOrder";
    }

//    // 前台渲染會員訂單
//    @GetMapping("/member/room/list")
//    public String getRoomRVOrdersFromMember(Model model, HttpSession session) {
//        // 從session抓出登入的會員物件
//         Members loginMember = (Members) session.getAttribute("member");
//        if (loginMember == null) {
//            return "redirect:/member/login";
//        }
//
//        // 查詢訂單
//        List<RoomRVOrder> orderList = roomRVOrderService.getRoomRVOrderByMember(loginMember);
//        model.addAttribute("orderList", orderList);
//
//        return "front-end/member/member-room-list";
//    }

    @Autowired
    MembersRepository membersRepository;
    @Autowired
    MembersService membersService;
    // 前台渲染會員訂單
    @GetMapping("/member/room/list")
    public String getRoomRVOrdersFromMember(Model model) {
        Members loginMember = membersService.getOneMember(8);
//        Members loginMember = membersRepository.findById(3);
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        // 查詢訂單
        List<RoomRVOrder> orderList = roomRVOrderService.getRoomRVOrderByMember(loginMember);
        model.addAttribute("orderList", orderList);

        return "front-end/member/member-room-list";
    }
}