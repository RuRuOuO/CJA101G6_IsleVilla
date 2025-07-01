package com.islevilla.ching.chat.controller;

import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.service.ChatRedisService;
import com.islevilla.yin.employee.model.Employee;

import jakarta.servlet.http.HttpSession;

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

	/** ✔ 聊天室列表（含未讀） */
	@GetMapping("/room/list")
	public String chatRoomList(HttpSession session, Model model) {
		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null) {
			return "redirect:/backend/auth";
		}

		Integer employeeId = employee.getEmployeeId();
		String employeeName = employee.getEmployeeName();

		List<ChatRoomDTO> rooms = chatRedisService.getAllChatRoomsWithUnread(employeeId)
				.stream()
                .filter(r -> r.getMemberName() != null && !"未知會員".equals(r.getMemberName()))
                .toList();
		
		model.addAttribute("rooms", rooms);
		model.addAttribute("employeeName", employeeName);

		return "back-end/chat/chatroomlist";
	}

	/** ✔ 聊天室詳細視窗 */
	@GetMapping("/room/{id}")
	public String chatRoomDetail(@PathVariable("id") Integer roomId, HttpSession session, Model model) {

		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null) {
			return "redirect:/backend/auth";
		}

		Integer employeeId = employee.getEmployeeId();
		String employeeName = employee.getEmployeeName();

		// ✔ 查聊天室資訊
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room == null) {
			model.addAttribute("error", "找不到該聊天室");
			return "back-end/chat/error";
		}

		// ✔ 清除未讀
		chatRedisService.clearUnread(roomId, employeeId);

		// ✔ 查訊息歷史
		List<ChatMessageDTO> messages = chatRedisService.getMessageHistory(roomId);

		model.addAttribute("room", room);
		model.addAttribute("messages", messages);
		model.addAttribute("roomId", roomId);
		model.addAttribute("senderId", employeeId);
		model.addAttribute("senderName", employeeName);
		model.addAttribute("senderType", 1);

		return "back-end/chat/chatroom";
	}

	/** ✔ 結束聊天室 */
	@PostMapping("/room/{id}/close")
	@ResponseBody
	public String closeChatRoom(@PathVariable("id") Integer roomId, HttpSession session) {

		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null) {
			return "未登入";
		}

		Integer employeeId = employee.getEmployeeId();

		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room == null) {
			return "聊天室不存在";
		}

		chatRedisService.closeChatRoom(roomId);
		return "聊天室已結束";
	}
	
}
