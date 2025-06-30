package com.islevilla.ching.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.service.ChatRedisService;

@Controller
@RequestMapping("/member/chat")
public class ChatMemberPageController {

    @Autowired
    private ChatRedisService chatRedisService;

    /** ✔ 點擊「線上客服」 → 建立聊天室 → 跳轉聊天室頁 */
    @GetMapping("/start")
    public String startChat(Model model) {
        ChatRoomDTO room = chatRedisService.createChatRoom(3001, "會員小明", 9001, "客服A");
        model.addAttribute("roomId", room.getChatRoomId());
        return "front-end/customer/onlineservice";
    }
}
