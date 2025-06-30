package com.islevilla.ching.chat.controller;

import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.service.ChatRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member/chat")
public class ChatMemberPageController {

    @Autowired
    private ChatRedisService chatRedisService;

    // ✔ 假會員ID（可以之後從登入Session動態取得）
    private final Integer MEMBER_ID = 3001;
    private final String MEMBER_NAME = "會員小明";

    // ✔ 預設對應客服（可以擴充為自動分配）
    private final Integer EMPLOYEE_ID = 9001;
    private final String EMPLOYEE_NAME = "客服A";

    /** ✔ 點擊「線上客服」 → 建立聊天室 → 跳轉聊天室頁 */
    @GetMapping("/start")
    public String startChat(Model model) {
        // ✔ 建立聊天室（若已存在則返回現有）
        ChatRoomDTO room = chatRedisService.createChatRoom(
                MEMBER_ID, MEMBER_NAME,
                EMPLOYEE_ID, EMPLOYEE_NAME
        );

        // ✔ 進入聊天室 → 清除會員端的未讀
        chatRedisService.clearUnread(room.getChatRoomId(), MEMBER_ID);

        // ✔ 傳遞參數給前端頁面
        model.addAttribute("roomId", room.getChatRoomId());
        model.addAttribute("senderId", MEMBER_ID);
        model.addAttribute("senderName", MEMBER_NAME);

        return "front-end/customer/onlineservice";
    }
}
