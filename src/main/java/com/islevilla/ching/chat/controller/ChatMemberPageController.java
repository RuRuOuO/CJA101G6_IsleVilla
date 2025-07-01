package com.islevilla.ching.chat.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.service.ChatRedisService;
import com.islevilla.lai.members.model.Members;
import com.islevilla.yin.employee.model.Employee;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member/chat")
public class ChatMemberPageController {

    @Autowired
    private ChatRedisService chatRedisService;

    /** ✔ 點擊「線上客服」 → 建立聊天室 → 跳轉聊天室頁 */
    @GetMapping("/start")
    public String startChat(HttpSession session, Model model) {
        // ✔ 從Session中取得會員資訊
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return "redirect:/member/login";  // 尚未登入 → 返回會員登入頁
        }

     // ✔ 從Session中取得客服資訊，如果沒有 → 指派預設客服
        Employee employee = (Employee) session.getAttribute("employee");

        Integer employeeId;
        String employeeName;

        if (employee != null) {
            employeeId = employee.getEmployeeId();
            employeeName = employee.getEmployeeName();
        } else {
            // ✔ 預設客服
            employeeId = 9001;
            employeeName = "IsleVilla 客服人員";
        }

        Integer memberId = member.getMemberId();
        String memberName = member.getMemberName();

        // ✔ 使用 getOrCreateChatRoom（自動判斷是否已有聊天室）
        ChatRoomDTO room = chatRedisService.createChatRoom(
                memberId, memberName,
                employeeId, employeeName
        );

        // ✔ 進入聊天室 → 清除會員端的未讀
        chatRedisService.clearUnread(room.getChatRoomId(), memberId);

        // ✔ 傳遞參數給前端頁面
        model.addAttribute("roomId", room.getChatRoomId());
        model.addAttribute("senderId", memberId);
        model.addAttribute("senderName", memberName);
        model.addAttribute("senderType", 0);  // ✔ 會員身份

        return "front-end/customer/onlineservice";
    }
}
