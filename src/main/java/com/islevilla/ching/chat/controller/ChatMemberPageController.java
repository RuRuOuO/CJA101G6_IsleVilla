package com.islevilla.ching.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomResult;
import com.islevilla.ching.chat.service.ChatRedisService;
import com.islevilla.ching.chat.service.ChatRoomUpdateService;
import com.islevilla.ching.chat.websocket.ChatWebSocketHandler;
import com.islevilla.lai.members.model.Members;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member/chat")
public class ChatMemberPageController {

	@Autowired
	private ChatRedisService chatRedisService;

	@Autowired
	private ChatRoomUpdateService chatRoomUpdateService;

	@Autowired
	private ChatWebSocketHandler chatWebSocketHandler;

	// 點擊線上客服 -> 建立聊天室 -> 跳轉聊天室頁
	@GetMapping("/start")
	public String startChat(HttpSession session, Model model) {
		// 從Session中取得會員資訊
		Members member = (Members) session.getAttribute("member");
		if (member == null) {
			return "redirect:/member/login"; // 未登入 返回會員登入
		}

		// 會員資訊
		Integer memberId = member.getMemberId();
		String memberName = member.getMemberName();

		ChatRoomResult result = chatRedisService.getOrCreateChatRoom(memberId, memberName, null, null // 不指定客服
		);

		ChatRoomDTO room = result.getRoom();

		// 進入聊天室就清除會員端的未讀
		chatRoomUpdateService.clearUnreadForMember(room.getChatRoomId());

		// 如果是新聊天室，就幾時廣播給後台顯示
		if (result.isNewlyCreated()) {
			chatWebSocketHandler.notifyNewRoom(room);
		}

		// 傳遞參數給前端頁面
		model.addAttribute("room", room);
		model.addAttribute("roomId", room.getChatRoomId());
		model.addAttribute("senderId", memberId);
		model.addAttribute("senderName", memberName);
		model.addAttribute("senderType", 0); // 0 = 會員

		return "front-end/customer/onlineservice";
	}
}