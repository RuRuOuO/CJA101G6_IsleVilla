package com.islevilla.ching.chat.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.service.ChatRedisService;
import com.islevilla.ching.chat.service.ChatRoomDbService;
import com.islevilla.ching.chat.service.ChatRoomUpdateService;
import com.islevilla.ching.chat.websocket.ChatWebSocketHandler;
import com.islevilla.yin.employee.model.Employee;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomApiController {

	private final ChatRoomUpdateService chatRoomUpdateService;
	private final ChatRedisService chatRedisService;
	private final ChatWebSocketHandler chatWebSocketHandler;
	private final ChatRoomDbService chatRoomDbService;

	// 查詢單一聊天室
	@GetMapping("/room/{roomId}")
	public ChatRoomDTO getRoom(@PathVariable Integer roomId) {
		return chatRedisService.getChatRoom(roomId);
	}

	// 查詢所有聊天室
	@GetMapping("/rooms")
	public List<ChatRoomDTO> getAllRooms() {
		return chatRedisService.getAllChatRoomsWithEmployeeUnread().stream()
				.filter(r -> r.getMemberName() != null && !"未知會員".equals(r.getMemberName())).sorted(Comparator
						.comparing(ChatRoomDTO::getLastMessageTime, Comparator.nullsFirst(Comparator.reverseOrder())))
				.toList();
	}

	// 查詢聊天室狀態
	@GetMapping("/room/{roomId}/status")
	public Map<String, Object> getRoomStatus(@PathVariable Integer roomId) {
		Integer status = chatRoomUpdateService.getChatRoomStatus(roomId);
		return Map.of("roomId", roomId, "chatStatus", status);
	}

	// 聊天室結案
	@PostMapping("/room/{id}/complete")
	public Map<String, Object> completeChatRoom(@PathVariable("id") Integer roomId, HttpSession session) {
		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null)
			return fail("未登入");

		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room == null)
			return fail("聊天室不存在");
		if (room.getChatStatus() == 2)
			return fail("聊天室已是結案狀態");

		// 狀態改為結案
		room.setChatStatus(2);
		chatRoomUpdateService.saveChatRoom(room);

		// 記錄結案時間
		chatRoomUpdateService.markRoomEndTime(roomId);

		// 廣播結案狀態給聊天室內的成員
		chatWebSocketHandler.broadcastRoomStatus(roomId, "roomComplete");

		log.info(" 聊天室 {} 已結案", roomId);
		return success("聊天室已結案");
	}

	@PostMapping("/room/{id}/mark-pending")
	public Map<String, Object> markRoomAsPending(@PathVariable("id") Integer roomId) {
	    chatRoomUpdateService.markRoomPending(roomId);
	    return Map.of("success", true);
	}
	
	// 聊天室重新開啟
	@PostMapping("/room/{id}/reopen")
	public Map<String, Object> reopenChatRoom(@PathVariable("id") Integer roomId, HttpSession session) {
		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null)
			return fail("未登入");

		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room == null)
			return fail("聊天室不存在");
		if (room.getChatStatus() == 1)
			return fail("聊天室已是進行中狀態");
		room.setChatStatus(1);
		chatRoomUpdateService.saveChatRoom(room);
		chatWebSocketHandler.broadcastRoomStatus(roomId, "roomReopen");

		log.info(" 聊天室 {} 已重新開啟", roomId);
		return success("聊天室已重新開啟");
	}

	// 查詢聊天室歷史訊息
	@GetMapping("/room/{roomId}/history")
	public List<ChatMessageDTO> getMessageHistory(@PathVariable Integer roomId,
			@RequestParam(name = "afterEnd", required = false, defaultValue = "false") boolean afterEnd) {
		return chatRedisService.getMessageHistory(roomId, afterEnd);
	}

	@GetMapping("/room/{roomId}/unread/employee")
	public Map<String, Object> getEmployeeUnread(@PathVariable Integer roomId) {
		Integer unread = chatRedisService.getUnreadCountForEmployee(roomId);
		return Map.of("roomId", roomId, "unreadType", "employee", "unreadCount", unread);
	}

	// 清除會員未讀
	@PostMapping("/room/{roomId}/unread/member/clear")
	public Map<String, Object> clearMemberUnread(@PathVariable Integer roomId) {
		chatRoomUpdateService.clearUnreadForMember(roomId);
		return success("會員未讀已清除");
	}

	// 清除客服未讀
	@PostMapping("/room/{roomId}/unread/employee/clear")
	public Map<String, Object> clearEmployeeUnread(@PathVariable Integer roomId) {
		chatRoomUpdateService.clearUnreadForEmployee(roomId);
		return success("客服未讀已清除");
	}

	// 刪除聊天室
	@DeleteMapping("/room/{roomId}/delete")
	public Map<String, Object> deleteRoom(@PathVariable Integer roomId) {
		chatRoomUpdateService.deleteChatRoom(roomId);
		return success("聊天室已刪除");
	}

	
	
	
	/* =================== 存入SQL =================== */
	@PostMapping("/room/{roomId}/import")
	public Map<String, Object> importRoomToSql(@PathVariable Integer roomId, HttpSession session) {
		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null)
			return fail("未登入");

		ChatRoomDTO redisRoom = chatRedisService.getChatRoom(roomId);
		if (redisRoom == null)
			return fail("查無此聊天室");

		List<ChatMessageDTO> redisMessages = chatRedisService.getMessageHistory(roomId, false);

		try {
			chatRoomDbService.saveRoomAndMessageToSql(redisRoom, redisMessages);
		} catch (Exception e) {
			log.error("匯入失敗 {}", roomId, e);
			return fail("匯入失敗：" + e.getMessage());
		}
		log.info("匯入成功 {}", roomId);
		return success("匯入成功" + roomId);
	}

	@GetMapping("/room/{roomId}/lastMessageTime")
	public Map<String, String> getLastMessageTime(@PathVariable Integer roomId) {
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room == null || room.getLastMessageTime() == null) {
			return Map.of("lastMessageTime", "-");
		}
		// 將 epoch 毫秒轉換成 LocalDateTime
		LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(room.getLastMessageTime()),
				ZoneId.systemDefault());
		String formatted = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		return Map.of("lastMessageTime", formatted);
	}
	
	
	/* =================== 共用回傳 =================== */

	private Map<String, Object> success(String msg) {
		return Map.of("success", true, "message", msg);
	}

	private Map<String, Object> fail(String msg) {
		return Map.of("success", false, "message", msg);
	}

}