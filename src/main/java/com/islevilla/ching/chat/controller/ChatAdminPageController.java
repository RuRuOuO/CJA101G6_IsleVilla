package com.islevilla.ching.chat.controller;

import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.service.ChatRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/backend/chat")
public class ChatAdminPageController {

    @Autowired
    private ChatRedisService chatRedisService;

    private final Integer EMPLOYEE_ID = 9001;
    private final String EMPLOYEE_NAME = "客服A";

    /** ✔ 聊天室列表（含未讀） */
    @GetMapping("/room/list")
    public String chatRoomList(Model model) {
        List<ChatRoomDTO> rooms = chatRedisService.getAllChatRoomsWithUnread(EMPLOYEE_ID);
        model.addAttribute("rooms", rooms);
        return "back-end/chat/chatroomlist";
    }

    /** ✔ 聊天室視窗 */
    @GetMapping("/room/{id}")
    public String chatRoomDetail(@PathVariable("id") Integer roomId, Model model) {
        // ✔ 進入聊天室時清除客服的未讀
        chatRedisService.clearUnread(roomId, EMPLOYEE_ID);

        model.addAttribute("roomId", roomId);
        model.addAttribute("senderId", EMPLOYEE_ID);
        model.addAttribute("senderName", EMPLOYEE_NAME);
        return "back-end/chat/chatroom";
    }
}
