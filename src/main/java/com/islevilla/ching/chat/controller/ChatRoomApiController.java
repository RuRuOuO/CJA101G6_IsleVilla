package com.islevilla.ching.chat.controller;

import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.service.ChatRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatRoomApiController {

    @Autowired
    private ChatRedisService chatRedisService;

    // ✔ 建立聊天室
    @PostMapping("/createRoom")
    public Map<String, Object> createRoom() {
        ChatRoomDTO room = chatRedisService.createChatRoom(
                3001, "會員小明",
                9001, "客服A"
        );
        return Map.of(
                "success", true,
                "roomId", room.getChatRoomId(),
                "message", "聊天室已建立"
        );
    }

    // ✔ 查詢單一聊天室
    @GetMapping("/room/{roomId}")
    public ChatRoomDTO getRoom(@PathVariable Integer roomId) {
        return chatRedisService.getChatRoom(roomId);
    }

    // ✔ 查詢所有聊天室
    @GetMapping("/rooms")
    public List<ChatRoomDTO> getAllRooms() {
        return chatRedisService.getAllChatRooms();
    }
}
