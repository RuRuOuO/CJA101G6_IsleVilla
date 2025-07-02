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

	// 聊天室列表頁面的Session
	private static final List<WebSocketSession> chatRoomListSessions = Collections.synchronizedList(new ArrayList<>());

	@Autowired
	private ChatRedisService chatRedisService;

	private final ObjectMapper mapper = new ObjectMapper();

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
			log.info(" WebSocket 連線成功 | Session {} 進入聊天室 {}", session.getId(), roomId);
		}
	}

	// 接收訊息
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		//解json訊息
		ChatMessageDTO chatMessage = mapper.readValue(message.getPayload(), ChatMessageDTO.class);

		if (chatMessage.getMessageContent() == null || chatMessage.getMessageContent().trim().isEmpty()) {
			session.sendMessage(new TextMessage(" 訊息內容不得為空"));
			return;
		}

		chatMessage.setMessageTime(System.currentTimeMillis());
		chatMessage.setIsRead(0);

		Integer roomId = chatMessage.getChatRoomId();
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);

		if (room == null) {
			session.sendMessage(new TextMessage(" 聊天室不存在 "));
			return;
		}
		
		if (room.getChatStatus() == 2 && chatMessage.getSenderType() == 0) {
	        room.setChatStatus(3);
	        chatRedisService.saveChatRoom(room);
	        log.info(" 聊天室 {} 狀態從已結案 -> 等待處理 ", roomId);
	    }


		// 訊息儲存
		chatRedisService.saveMessage(roomId, chatMessage);
		
		// 更新最後發送訊息時間
		chatRedisService.updateLastMessageTime(roomId, chatMessage.getMessageTime());

		// 廣播到聊天室內
		List<WebSocketSession> sessions = chatRoomSessions.getOrDefault(roomId, Collections.emptyList());
		for (WebSocketSession s : sessions) {
			if (s.isOpen()) {
				s.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessage)));
			}
		}

		log.info(" 廣播訊息：聊天室 {} | {}：{}", roomId, chatMessage.getSenderName(), chatMessage.getMessageContent());

		// 廣播到聊天室列表頁 → 更新未讀數
		broadcastToRoomList(roomId);
	}

	// 廣播聊天室狀態（完成/重新開啟）
	public void broadcastRoomStatus(Integer roomId, String action) {
		Map<String, Object> payload = Map.of("type", "system", "action", action, // roomComplete 或 roomReopen
				"roomId", roomId);
		String json;
		try {
			json = mapper.writeValueAsString(payload);
		} catch (Exception e) {
			log.error(" JSON 轉換失敗 {}", e.getMessage());
			return;
		}

		List<WebSocketSession> sessions = chatRoomSessions.getOrDefault(roomId, Collections.emptyList());
		for (WebSocketSession s : sessions) {
			if (s.isOpen()) {
				try {
					s.sendMessage(new TextMessage(json));
				} catch (Exception e) {
					log.error("廣播聊天室狀態失敗 {}", e.getMessage());
				}
			}
		}

		log.info(" 廣播聊天室 {} 狀態更新：{}", roomId, action);
	}

	// 廣播到聊天室列表 → 更新未讀
	private void broadcastToRoomList(Integer roomId) {
		Map<String, Object> payload = Map.of("type", "newMessage", "roomId", roomId);
		String json;
		try {
			json = mapper.writeValueAsString(payload);
		} catch (Exception e) {
			log.error(" JSON 轉換失敗 {}", e.getMessage());
			return;
		}

		for (WebSocketSession session : chatRoomListSessions) {
			if (session.isOpen()) {
				try {
					session.sendMessage(new TextMessage(json));
				} catch (Exception e) {
					log.error(" 廣播至聊天室列表失敗 {}", e.getMessage());
				}
			}
		}
	}

	// 離線
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		String uri = Objects.requireNonNull(session.getUri()).toString();
		if (uri.contains("/ws/chatroomList")) {
			chatRoomListSessions.remove(session);
			log.info(" Session {} 離開聊天室列表頁", session.getId());
		} else {
			try {
				Integer roomId = getParam(session, "roomId");
				List<WebSocketSession> sessions = chatRoomSessions.getOrDefault(roomId, new ArrayList<>());
				sessions.remove(session);
				if (sessions.isEmpty()) {
					chatRoomSessions.remove(roomId);
				}
				log.info(" Session {} 離開聊天室 {}", session.getId(), roomId);
			} catch (Exception e) {
				log.error(" 關閉WebSocket時發生錯誤: {}", e.getMessage());
			}
		}
	}

	// 錯誤處理
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log.error(" WebSocket 錯誤：{}", exception.getMessage());
	}

	// 取得參數
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
			log.error(" 解析參數失敗：{}", e.getMessage());
		}
		throw new IllegalArgumentException(" 缺少必要參數：" + key);
	}

}