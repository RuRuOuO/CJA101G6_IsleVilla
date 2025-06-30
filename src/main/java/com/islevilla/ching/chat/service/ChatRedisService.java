package com.islevilla.ching.chat.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;

@Service
public class ChatRedisService {

    @Autowired
    @Qualifier("redisStringTemplate")
    private RedisTemplate<String, String> redisStr;

    private final Gson gson = new Gson();

    /* ================= 聊天室 ================= */

    /** ✔ 建立聊天室 */
    public ChatRoomDTO createChatRoom(Integer memberId, String memberName,
                                      Integer employeeId, String employeeName) {
        int roomId = incrementChatRoomId();

        ChatRoomDTO room = new ChatRoomDTO(
                roomId, memberId, memberName,
                employeeId, employeeName,
                1 // chatStatus=進行中
        );

        // 儲存聊天室
        redisStr.opsForValue().set(ChatRedisKey.chatRoom(roomId), gson.toJson(room));

        // 加入聊天室列表
        redisStr.opsForSet().add(ChatRedisKey.CHAT_ROOMS_SET, String.valueOf(roomId));

        return room;
    }

    /** ✔ 查詢單一聊天室 */
    public ChatRoomDTO getChatRoom(Integer roomId) {
        String json = redisStr.opsForValue().get(ChatRedisKey.chatRoom(roomId));
        return json == null ? null : gson.fromJson(json, ChatRoomDTO.class);
    }

    /** ✔ 查詢所有聊天室 */
    public List<ChatRoomDTO> getAllChatRooms() {
        Set<String> ids = redisStr.opsForSet().members(ChatRedisKey.CHAT_ROOMS_SET);
        if (ids == null) return Collections.emptyList();

        return ids.stream()
                .map(id -> getChatRoom(Integer.parseInt(id)))
                .filter(Objects::nonNull)
                .toList();
    }

    /** ✔ 結束聊天室 */
    public void closeChatRoom(Integer roomId) {
        ChatRoomDTO room = getChatRoom(roomId);
        if (room != null) {
            room.setChatStatus(0);
            redisStr.opsForValue().set(ChatRedisKey.chatRoom(roomId), gson.toJson(room));
        }
    }

    /** ✔ 刪除聊天室（包含歷史訊息） */
    public void deleteChatRoom(Integer roomId) {
        redisStr.delete(ChatRedisKey.chatRoom(roomId));
        redisStr.delete(ChatRedisKey.chatMessages(roomId));
        redisStr.delete(ChatRedisKey.chatUnread(roomId));
        redisStr.opsForSet().remove(ChatRedisKey.CHAT_ROOMS_SET, String.valueOf(roomId));
    }

    /** ✔ 自增聊天室ID */
    public Integer incrementChatRoomId() {
        return Optional.ofNullable(redisStr.opsForValue().increment(ChatRedisKey.chatRoomSeq()))
                .orElse(1L)
                .intValue();
    }

    /* ================= 訊息 ================= */

    /** ✔ 儲存訊息 */
    public void saveMessage(Integer roomId, ChatMessageDTO message) {
        redisStr.opsForList().rightPush(ChatRedisKey.chatMessages(roomId), gson.toJson(message));

        // ✔ 更新未讀計數
        redisStr.opsForValue().increment(ChatRedisKey.chatUnread(roomId));
    }

    /** ✔ 查詢歷史訊息 */
    public List<ChatMessageDTO> getMessageHistory(Integer roomId) {
        List<String> list = redisStr.opsForList().range(ChatRedisKey.chatMessages(roomId), 0, -1);
        if (list == null) return new ArrayList<>();

        return list.stream()
                .map(json -> gson.fromJson(json, ChatMessageDTO.class))
                .toList();
    }

    /* ================= 未讀計數 ================= */

    /** ✔ 查詢未讀 */
    public Integer getUnreadCount(Integer roomId) {
        String count = redisStr.opsForValue().get(ChatRedisKey.chatUnread(roomId));
        return count == null ? 0 : Integer.parseInt(count);
    }

    /** ✔ 清除未讀 */
    public void clearUnread(Integer roomId) {
        redisStr.delete(ChatRedisKey.chatUnread(roomId));
    }
}

