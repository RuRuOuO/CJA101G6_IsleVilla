package com.islevilla.ching.chat.websocket;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
import com.islevilla.ching.chat.service.ChatRedisService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

	// 每個聊天室的所有session
	private static final Map<Integer, List<WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();

	@Autowired
	private ChatRedisService chatRedisService;

	private final ObjectMapper mapper = new ObjectMapper();

	// 建立連線
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		Integer roomId = getParam(session, "roomId");
		chatRoomSessions.computeIfAbsent(roomId, k -> new ArrayList<>()).add(session);
		log.info("✅ WebSocket 連線成功 | Session {} 進入聊天室 {}", session.getId(), roomId);
	}

	// 接收訊息
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		ChatMessageDTO chatMessage = mapper.readValue(message.getPayload(), ChatMessageDTO.class);

		if (chatMessage.getMessageContent() == null || chatMessage.getMessageContent().trim().isEmpty()) {
			session.sendMessage(new TextMessage("❌ 訊息內容不得為空"));
			return;
		}

		chatMessage.setMessageTime(System.currentTimeMillis());
		chatMessage.setIsRead(0);

		Integer roomId = chatMessage.getChatRoomId();

		// 取得聊天室
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);

		if (room == null) {
			session.sendMessage(new TextMessage("❌ 聊天室不存在"));
			return;
		}

		// 若聊天室已結束，禁止傳送
		if (room.getChatStatus() == 0) {
			session.sendMessage(new TextMessage("❌ 此聊天室已結束，無法傳送訊息。"));
			return;
		}

		// 儲存訊息到redis
		chatRedisService.saveMessage(roomId, chatMessage);

		// 廣播
		List<WebSocketSession> sessions = chatRoomSessions.getOrDefault(roomId, Collections.emptyList());
		for (WebSocketSession s : sessions) {
			if (s.isOpen()) {
				s.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessage)));
			}
		}

		log.info("廣播訊息：聊天室 {} | {}：{}", roomId, chatMessage.getSenderName(), chatMessage.getMessageContent());
	}

	// 關閉連線
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		Integer roomId = null;
		try {
			roomId = getParam(session, "roomId");
			List<WebSocketSession> sessions = chatRoomSessions.getOrDefault(roomId, new ArrayList<>());
			sessions.remove(session);

			if (sessions.isEmpty()) {
				chatRoomSessions.remove(roomId);
			}
			log.info("❌ Session {} 離開聊天室 {}", session.getId(), roomId);
		} catch (Exception e) {
			log.error("❌ 關閉WebSocket時發生錯誤: {}", e.getMessage());
		}
	}

	// 處理異常
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log.error("❌ WebSocket 錯誤：{}", exception.getMessage());
	}

	private Integer getParam(WebSocketSession session, String key) {
		try {
			String uri = Objects.requireNonNull(session.getUri()).toString();
			String[] parts = uri.split("\\?");
			if (parts.length > 1) {
				String[] params = parts[1].split("&");
				for (String param : params) {
					if (param.startsWith(key + "=")) {
						return Integer.parseInt(param.split("=")[1]);
					}
				}
			}
		} catch (Exception e) {
			log.error("❌ 解析參數失敗：{}", e.getMessage());
		}
		throw new IllegalArgumentException("❌ 缺少必要參數：" + key);
	}
}