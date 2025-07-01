package com.islevilla.ching.chat.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.service.ChatRedisService;
import com.islevilla.yin.employee.model.Employee;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/backend/chat")
public class ChatAdminPageController {

	@Autowired
	private ChatRedisService chatRedisService;

	// 聊天室列表（含未讀）
	@GetMapping("/room/list")
	public String chatRoomList(HttpSession session, Model model) {
		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null) {
			return "redirect:/backend/auth";
		}

		String employeeName = employee.getEmployeeName();

		List<ChatRoomDTO> rooms = chatRedisService.getAllChatRoomsWithEmployeeUnread().stream()
				.filter(r -> r.getMemberName() != null && !"未知會員".equals(r.getMemberName())).toList();

		model.addAttribute("rooms", rooms);
		model.addAttribute("employeeName", employeeName);

		return "back-end/chat/chatroomlist";
	}

	// 聊天室詳細視窗
	@GetMapping("/room/{id}")
	public String chatRoomDetail(@PathVariable("id") Integer roomId, HttpSession session, Model model) {

		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null) {
			return "redirect:/backend/auth";
		}

		Integer employeeId = employee.getEmployeeId();
		String employeeName = employee.getEmployeeName();

		// 查聊天室資訊
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room == null) {
			model.addAttribute("error", "找不到該聊天室");
			return "back-end/chat/error";
		}
		// 誰最後進入聊天室，聊天室客服就是誰
		room.setEmployeeId(employeeId);
		room.setEmployeeName(employeeName);
		chatRedisService.saveChatRoom(room); // redis直接更新

		// 清除未讀
		chatRedisService.clearUnreadForEmployee(roomId);

		// 查訊息歷史
		List<ChatMessageDTO> messages = chatRedisService.getMessageHistory(roomId);

		model.addAttribute("chatStatus", room.getChatStatus());
		model.addAttribute("room", room); 
        model.addAttribute("roomId", roomId);
        model.addAttribute("messages", messages);
        model.addAttribute("senderId", employeeId);
        model.addAttribute("senderName", employeeName);
        model.addAttribute("senderType", 1); // 1 = 客服端

		return "back-end/chat/chatroom";
	}

	// 結束聊天室
	@PostMapping("/room/{id}/close")
	@ResponseBody
	public Map<String, Object> closeChatRoom(@PathVariable("id") Integer roomId, HttpSession session) {

		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null) {
			return Map.of("success", false, "message", "未登入");
		}

		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room == null) {
			return Map.of("success", false, "message", "聊天室不存在");
		}

		chatRedisService.closeChatRoom(roomId);
		return Map.of("success", true, "message", "聊天室已結束");
	}

	/** ✔ 重新開啟聊天室 */
	@PostMapping("/room/{id}/open")
	@ResponseBody
	public Map<String, Object> openChatRoom(@PathVariable("id") Integer roomId, HttpSession session) {
		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null) {
			return Map.of("success", false, "message", "未登入");
		}

		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room == null) {
			return Map.of("success", false, "message", "聊天室不存在");
		}

		room.setChatStatus(1); // ✔ 狀態改為進行中
		chatRedisService.saveChatRoom(room);

		return Map.of("success", true, "message", "聊天室已重新開啟");
	}
}
