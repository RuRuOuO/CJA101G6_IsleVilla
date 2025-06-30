package com.islevilla.ching.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.service.ChatRedisService;

@Controller
@RequestMapping("/backend/chat")
public class ChatAdminPageController {

    @Autowired
    private ChatRedisService chatRedisService;

    /** ✔ 聊天室列表 */
    @GetMapping("/room/list")
    public String chatRoomList(Model model) {
        List<ChatRoomDTO> rooms = chatRedisService.getAllChatRooms();
        model.addAttribute("rooms", rooms);
        return "back-end/chat/chatroomlist";
    }

    /** ✔ 聊天室視窗（roomId從Path傳入） */
    @GetMapping("/room/{id}")
    public String chatRoomDetail(@PathVariable("id") Integer roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "back-end/chat/chatroom";
    }
}

