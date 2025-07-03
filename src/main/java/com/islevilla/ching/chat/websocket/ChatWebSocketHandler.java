package com.islevilla.ching.chat.websocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomResult;
import com.islevilla.ching.chat.service.ChatRedisService;
import com.islevilla.ching.chat.service.ChatRoomUpdateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

	@Autowired
	private ChatRedisService chatRedisService;

	@Autowired
	private ChatRoomUpdateService chatRoomUpdateService;

	private final ObjectMapper mapper = new ObjectMapper();

	// 每個聊天室的所有 WebSocketSession
	private static final Map<Integer, List<WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();

	// 後台聊天室列表頁的 WebSocketSession（chatroomlist.html）
	private static final List<WebSocketSession> chatRoomListSessions = Collections.synchronizedList(new ArrayList<>());

	// 建立連線
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		String uri = Objects.requireNonNull(session.getUri()).toString();

		if (uri.contains("/ws/chatroomList")) {
			chatRoomListSessions.add(session);
			log.info(" WebSocket 連線成功 | ChatRoomList | Session {}", session.getId());
		} else {
			Integer roomId = getParam(session, "roomId");
			chatRoomSessions.computeIfAbsent(roomId, k -> new ArrayList<>()).add(session);
			log.info("✅ 已加入聊天室 {} 的 session：{}", roomId, session.getId());
		}
	}

	// 接收訊息
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		// 解json訊息
		ChatMessageDTO chatMessage = mapper.readValue(message.getPayload(), ChatMessageDTO.class);

		// 從連線 URI 中取得 roomId
		Integer roomId = getParam(session, "roomId");
		if (roomId == null) {
			log.warn(" 找不到 roomId，無法處理訊息");
			return;
		}
		// 設定補充欄位
		chatMessage.setChatRoomId(roomId);
		chatMessage.setMessageTime(System.currentTimeMillis());
		chatMessage.setIsRead(0);

		// 儲存訊息
		chatRedisService.saveMessage(roomId, chatMessage);

		// 廣播給聊天室內所有人
		List<WebSocketSession> sessions = chatRoomSessions.getOrDefault(roomId, Collections.emptyList());
		for (WebSocketSession s : sessions) {
			if (s.isOpen()) {
				s.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessage)));
			}
		}
		// 廣播聊天室列表頁面（未讀數更新）
		broadcastToRoomList("newMessage", roomId, null);
	}

	// 廣播聊天室狀態（完成/重新開啟）
	public void broadcastRoomStatus(Integer roomId, String action) {
		Map<String, Object> payload = Map.of("type", "system", "action", action, "roomId", roomId); // roomComplete 或
																									// roomReopen

		try {
			String json = mapper.writeValueAsString(payload);
			for (WebSocketSession s : chatRoomSessions.getOrDefault(roomId, Collections.emptyList())) {
				if (s.isOpen())
					s.sendMessage(new TextMessage(json));
			}
			log.info("廣播聊天室 {} 狀態更新：{}", roomId, action);
		} catch (Exception e) {
			log.error("廣播聊天室狀態失敗：{}", e.getMessage());
		}
	}

	// 廣播到聊天室列表(更新未讀)
	// 廣播至聊天室列表頁（更新列表顯示）
	private void broadcastToRoomList(String type, Integer roomId, ChatRoomDTO roomData) {
		try {
			Map<String, Object> data = new HashMap<>();
			data.put("type", type);

			if (roomId != null) {
				data.put("roomId", roomId);
			}

			if (roomData != null) {
				data.put("room", roomData);
			}

			String payload = mapper.writeValueAsString(data);
			for (WebSocketSession s : chatRoomListSessions) {
				if (s.isOpen()) {
					s.sendMessage(new TextMessage(payload));
				}
			}
		} catch (Exception e) {
			log.error("❌ 廣播聊天室列表失敗", e);
		}
	}

	// 從 WebSocket URI 中解析 query string，例如 ?roomId=5
	private Integer getParam(WebSocketSession session, String param) {
		try {
			String query = session.getUri().getQuery(); // roomId=5
			for (String pair : query.split("&")) {
				String[] parts = pair.split("=");
				if (parts.length == 2 && parts[0].equals(param)) {
					return Integer.parseInt(parts[1]);
				}
			}
		} catch (Exception e) {
			log.error("❌ getParam 發生錯誤", e);
		}
		return null;
	}

	// 外部觸發：新增聊天室時廣播
	public void notifyNewRoom(ChatRoomDTO newRoom) {
		broadcastToRoomList("newRoom", null, newRoom);
	}

	// 離線
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		chatRoomListSessions.remove(session);

		chatRoomSessions.values().forEach(list -> list.remove(session));

		log.info(" WebSocket 已關閉 | Session {}", session.getId());
	}

	// 錯誤處理
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log.error(" WebSocket 錯誤：{}", exception.getMessage());
	}

}