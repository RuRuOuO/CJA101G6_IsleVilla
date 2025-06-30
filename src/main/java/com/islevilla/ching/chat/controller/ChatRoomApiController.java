package com.islevilla.ching.chat.controller;

import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
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

    /** ✔ 建立聊天室 */
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

    /** ✔ 查詢單一聊天室 */
    @GetMapping("/room/{roomId}")
    public ChatRoomDTO getRoom(@PathVariable Integer roomId) {
        return chatRedisService.getChatRoom(roomId);
    }

    /** ✔ 查詢所有聊天室 */
    @GetMapping("/rooms")
    public List<ChatRoomDTO> getAllRooms() {
        return chatRedisService.getAllChatRooms();
    }

    /** ✔ 查詢聊天室歷史訊息 */
    @GetMapping("/room/{roomId}/history")
    public List<ChatMessageDTO> getMessageHistory(@PathVariable Integer roomId) {
        return chatRedisService.getMessageHistory(roomId);
    }

    /** ✔ 查詢未讀訊息 */
    @GetMapping("/room/{roomId}/unread/{userId}")
    public Map<String, Object> getUnread(@PathVariable Integer roomId, @PathVariable Integer userId) {
        Integer unread = chatRedisService.getUnreadCount(roomId, userId);
        return Map.of(
                "roomId", roomId,
                "userId", userId,
                "unreadCount", unread
        );
    }

    /** ✔ 清除未讀 */
    @PostMapping("/room/{roomId}/unread/{userId}/clear")
    public Map<String, Object> clearUnread(@PathVariable Integer roomId, @PathVariable Integer userId) {
        chatRedisService.clearUnread(roomId, userId);
        return Map.of(
                "success", true,
                "message", "未讀訊息已清除"
        );
    }

    /** ✔ 關閉聊天室 */
    @PostMapping("/room/{roomId}/close")
    public Map<String, Object> closeRoom(@PathVariable Integer roomId) {
        ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
        if (room != null) {
            room.setChatStatus(0); // ✔ 狀態改為結束
            chatRedisService.saveChatRoom(room);
            return Map.of(
                    "success", true,
                    "message", "聊天室已關閉"
            );
        } else {
            return Map.of(
                    "success", false,
                    "message", "聊天室不存在"
            );
        }
    }

    /** ✔ 刪除聊天室 */
    @DeleteMapping("/room/{roomId}/delete")
    public Map<String, Object> deleteRoom(@PathVariable Integer roomId) {
        chatRedisService.deleteChatRoom(roomId);
        return Map.of(
                "success", true,
                "message", "聊天室已刪除"
        );
    }

    /** ✔ 開啟聊天室 */
    @PostMapping("/room/{roomId}/open")
    public Map<String, Object> openRoom(@PathVariable Integer roomId) {
        ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
        if (room != null) {
            room.setChatStatus(1);
            chatRedisService.saveChatRoom(room);
            return Map.of(
                    "success", true,
                    "message", "聊天室已開啟"
            );
        } else {
            return Map.of(
                    "success", false,
                    "message", "聊天室不存在"
            );
        }
    }
}
