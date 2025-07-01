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

	// 查詢單一聊天室
	@GetMapping("/room/{roomId}")
	public ChatRoomDTO getRoom(@PathVariable Integer roomId) {
		return chatRedisService.getChatRoom(roomId);
	}

	// 查詢所有聊天室
	@GetMapping("/rooms")
	public List<ChatRoomDTO> getAllRooms() {
		return chatRedisService.getAllChatRooms();
	}

	// 查詢聊天室歷史訊息
	@GetMapping("/room/{roomId}/history")
	public List<ChatMessageDTO> getMessageHistory(@PathVariable Integer roomId) {
		return chatRedisService.getMessageHistory(roomId);
	}

	// 查詢會員未讀訊息
	@GetMapping("/room/{roomId}/unread/{userId}")
	public Map<String, Object> getMemberUnread(@PathVariable Integer roomId) {
		Integer unread = chatRedisService.getUnreadCountForMember(roomId);
		return Map.of("roomId", roomId, "unreadType", "member", "unreadCount", unread);
	}

	// 查詢客服的未讀
	@GetMapping("/room/{roomId}/unread/employee")
	public Map<String, Object> getEmployeeUnread(@PathVariable Integer roomId) {
		Integer unread = chatRedisService.getUnreadCountForEmployee(roomId);
		return Map.of("roomId", roomId, "unreadType", "employee", "unreadCount", unread);
	}

	// 清除會員未讀
	@PostMapping("/room/{roomId}/unread/member/clear")
	public Map<String, Object> clearMemberUnread(@PathVariable Integer roomId) {
		chatRedisService.clearUnreadForMember(roomId);
		return Map.of("success", true, "message", "會員未讀已清除");
	}

	// 清除客服未讀
	@PostMapping("/room/{roomId}/unread/employee/clear")
	public Map<String, Object> clearEmployeeUnread(@PathVariable Integer roomId) {
		chatRedisService.clearUnreadForEmployee(roomId);
		return Map.of("success", true, "message", "客服未讀已清除");
	}

	// 開啟聊天室 
	@PostMapping("/room/{roomId}/open")
	public Map<String, Object> openRoom(@PathVariable Integer roomId) {
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room != null) {
			room.setChatStatus(1);
			chatRedisService.saveChatRoom(room);
			return Map.of(
					"success", true, 
					"message", "聊天室已開啟");
		} else {
			return Map.of(
					"success", false, 
					"message", "聊天室不存在"
			);
		}
	}

	// 關閉聊天室
	@PostMapping("/room/{roomId}/close")
	public Map<String, Object> closeRoom(@PathVariable Integer roomId) {
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room != null) {
			room.setChatStatus(0); // 狀態改為結束
			chatRedisService.saveChatRoom(room);
			return Map.of("success", true, "message", "聊天室已關閉");
		} else {
			return Map.of("success", false, "message", "聊天室不存在");
		}
	}

	// 刪除聊天室 
	@DeleteMapping("/room/{roomId}/delete")
	public Map<String, Object> deleteRoom(@PathVariable Integer roomId) {
		chatRedisService.deleteChatRoom(roomId);
		return Map.of(
				"success", true, 
				"message", "聊天室已刪除"
		);
	}
}
