package com.islevilla.ching.chat.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.service.ChatRedisService;
import com.islevilla.ching.chat.service.ChatRoomUpdateService;
import com.islevilla.yin.employee.model.Employee;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/backend/chat")
public class ChatAdminPageController {

	@Autowired
	private ChatRedisService chatRedisService;

	@Autowired
	private ChatRoomUpdateService chatRoomUpdateService;

	// 聊天室列表（含未讀）
	@GetMapping("/room/list")
	public String chatRoomList(HttpSession session, Model model) {
		System.out.println("進入 chatRoomList controller");
		Employee employee = (Employee) session.getAttribute("employee");
		if (employee == null) {
			return "redirect:/backend/auth";
		}

		// 呼叫 Redis 抓聊天室
		List<ChatRoomDTO> rooms = chatRedisService.getAllChatRoomsWithEmployeeUnread();
		System.out.println(" Redis 抓到聊天室數量：" + rooms.size());

		for (ChatRoomDTO r : rooms) {
			System.out.println(" roomId = " + r.getChatRoomId() + ", memberId = " + r.getMemberId() + ", memberName = "
					+ r.getMemberName() + ", employeeId = " + r.getEmployeeId() + ", lastMsgTime = "
					+ r.getLastMessageTime());
		}

		// 過濾未知會員 + 排序
		rooms = rooms.stream().filter(r -> r.getMemberName() != null && !"未知會員".equals(r.getMemberName()))
				.sorted(Comparator.comparing(r -> Optional.ofNullable(r.getLastMessageTime()).orElse(0L),
						Comparator.reverseOrder()))
				.toList();
		System.out.println("過濾後聊天室數量：" + rooms.size());

		model.addAttribute("rooms", rooms);
		model.addAttribute("employeeName", employee.getEmployeeName());

		return "back-end/chat/chatroomlist";
	}

	// 聊天室對話視窗
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

		// 如果狀態是等待處理 → 自動轉回進行中
		if (room.getChatStatus() == 3) {
			room.setChatStatus(1);
		}

		// 誰最後進入聊天室，聊天室客服就是誰
		room.setEmployeeId(employeeId);
		room.setEmployeeName(employeeName);
		chatRoomUpdateService.saveChatRoom(room); // Redis 更新

		// 清除未讀
		chatRoomUpdateService.clearUnreadForEmployee(roomId); // 修正使用正確 Service

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

	@GetMapping("/history/{roomId}")
	public String viewChatHistory(@PathVariable Integer roomId, Model model) {

		// 從redis撈聊天室的基本資料
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);

		// 從redis撈資料出來 選定聊天室的歷史訊息
		List<ChatMessageDTO> messages = chatRedisService.getMessageHistory(roomId);

		System.out.println("room = " + room);
		System.out.println("messages = " + messages.size());
		
		model.addAttribute("room", room);
		model.addAttribute("messages", messages);
		return "back-end/chat/chat_history";
	}

}
