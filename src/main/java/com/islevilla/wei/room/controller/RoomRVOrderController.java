package com.islevilla.wei.room.controller;

import com.islevilla.wei.PageUtil;
import com.islevilla.wei.room.model.RoomRVOrder;
import com.islevilla.wei.room.model.RoomRVOrderService;
import jakarta.servlet.http.HttpServletRequest;
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

@Controller
public class RoomRVOrderController {
    @Autowired
    private RoomRVOrderService roomRVOrderService;

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
        RoomRVOrder roomRVOrder = roomRVOrderService.getById(id);
        model.addAttribute("roomRVOrder", roomRVOrder);
        return "back-end/roomRVOrder/listOneRoomRVOrder";
    }
}