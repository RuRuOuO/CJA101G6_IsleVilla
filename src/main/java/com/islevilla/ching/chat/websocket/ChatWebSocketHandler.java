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
import com.islevilla.ching.chat.service.ChatRedisService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // ✔ 每個聊天室對應多個Session
    private static final Map<Integer, List<WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();

    @Autowired
    private ChatRedisService chatRedisService;

    private final ObjectMapper mapper = new ObjectMapper();

    /** ✔ 建立連線 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer roomId = getRoomId(session);
        chatRoomSessions.computeIfAbsent(roomId, k -> new ArrayList<>()).add(session);
        log.info("✅ 用戶 {} 已加入聊天室 {}", session.getId(), roomId);
    }

    /** ✔ 接收訊息 */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            Integer roomId = getRoomId(session);

            // ✔ 解析訊息
            ChatMessageDTO chatMessage = mapper.readValue(message.getPayload(), ChatMessageDTO.class);
            chatMessage.setMessageTime(System.currentTimeMillis());
            chatMessage.setIsRead(0);

            // ✔ 儲存到 Redis
            chatRedisService.saveMessage(roomId, chatMessage);

            // ✔ 廣播到聊天室
            broadcastToRoom(roomId, chatMessage);

            log.info("📤 廣播訊息到聊天室 {}: {}", roomId, chatMessage.getMessageContent());

        } catch (Exception e) {
            log.error("❌ 處理訊息發生錯誤", e);
        }
    }

    /** ✔ 關閉連線 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer roomId = getRoomId(session);
        chatRoomSessions.getOrDefault(roomId, new ArrayList<>()).remove(session);
        log.info("❎ 用戶 {} 離開聊天室 {}", session.getId(), roomId);
    }

    /** ✔ 廣播訊息到聊天室 */
    private void broadcastToRoom(Integer roomId, ChatMessageDTO message) {
        List<WebSocketSession> sessions = chatRoomSessions.getOrDefault(roomId, Collections.emptyList());
        String jsonMessage;
        try {
            jsonMessage = mapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("❌ 訊息序列化失敗", e);
            return;
        }

        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(jsonMessage));
                } catch (Exception e) {
                    log.warn("⚠️ 傳送訊息給用戶 {} 失敗", s.getId(), e);
                }
            }
        }
    }

    /** ✔ 從 URL 查詢參數提取 roomId */
    private Integer getRoomId(WebSocketSession session) {
        String uri = Objects.requireNonNull(session.getUri()).toString();
        String[] parts = uri.split("\\?");
        if (parts.length > 1) {
            String[] params = parts[1].split("&");
            for (String param : params) {
                if (param.startsWith("roomId=")) {
                    return Integer.parseInt(param.split("=")[1]);
                }
            }
        }
        throw new IllegalArgumentException("WebSocket URL 缺少 roomId 參數");
    }
}
