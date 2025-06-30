package com.islevilla.ching.chat.service;

import com.google.gson.Gson;
import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatRedisService {

    @Autowired
    @Qualifier("redisStringTemplate")
    private RedisTemplate<String, String> redisStr;

    private final Gson gson = new Gson();

    /* ================= 聊天室管理 ================= */

    /** ✔ 建立聊天室 */
    public ChatRoomDTO createChatRoom(Integer memberId, String memberName,
                                      Integer employeeId, String employeeName) {

        int roomId = incrementChatRoomId();

        ChatRoomDTO room = ChatRoomDTO.builder()
                .chatRoomId(roomId)
                .memberId(memberId)
                .memberName(memberName)
                .employeeId(employeeId)
                .employeeName(employeeName)
                .chatStatus(1) // 1 = 進行中
                .unreadCount(0) // 預設為0
                .build();

        // ✔ 儲存聊天室
        redisStr.opsForValue().set(ChatRedisKey.chatRoom(roomId), gson.toJson(room));

        // ✔ 加入聊天室列表
        redisStr.opsForSet().add(ChatRedisKey.CHAT_ROOMS_SET, String.valueOf(roomId));

        return room;
    }

    /** ✔ 查詢單一聊天室 */
    public ChatRoomDTO getChatRoom(Integer roomId) {
        String json = redisStr.opsForValue().get(ChatRedisKey.chatRoom(roomId));
        return json == null ? null : gson.fromJson(json, ChatRoomDTO.class);
    }

    /** ✔ 查詢所有聊天室（不帶未讀） */
    public List<ChatRoomDTO> getAllChatRooms() {
        Set<String> ids = redisStr.opsForSet().members(ChatRedisKey.CHAT_ROOMS_SET);
        if (ids == null) return Collections.emptyList();

        return ids.stream()
                .map(id -> getChatRoom(Integer.parseInt(id)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /** ✔ 查詢所有聊天室（帶未讀） */
    public List<ChatRoomDTO> getAllChatRoomsWithUnread(Integer userId) {
        return getAllChatRooms().stream()
                .map(room -> {
                    int unread = getUnreadCount(room.getChatRoomId(), userId);
                    room.setUnreadCount(unread);
                    return room;
                })
                .collect(Collectors.toList());
    }

    /** ✔ 結束聊天室 */
    public void closeChatRoom(Integer roomId) {
        ChatRoomDTO room = getChatRoom(roomId);
        if (room != null) {
            room.setChatStatus(0); // 0 = 結束
            redisStr.opsForValue().set(ChatRedisKey.chatRoom(roomId), gson.toJson(room));
        }
    }

    /** ✔ 刪除聊天室（含訊息與未讀） */
    public void deleteChatRoom(Integer roomId) {
        redisStr.delete(ChatRedisKey.chatRoom(roomId));
        redisStr.delete(ChatRedisKey.chatMessages(roomId));

        ChatRoomDTO room = getChatRoom(roomId);
        if (room != null) {
            redisStr.delete(ChatRedisKey.chatUnread(roomId, room.getMemberId()));
            redisStr.delete(ChatRedisKey.chatUnread(roomId, room.getEmployeeId()));
        }

        redisStr.opsForSet().remove(ChatRedisKey.CHAT_ROOMS_SET, String.valueOf(roomId));
    }

    /** ✔ 自增聊天室ID */
    public Integer incrementChatRoomId() {
        return Optional.ofNullable(redisStr.opsForValue().increment(ChatRedisKey.chatRoomSeq()))
                .orElse(1L).intValue();
    }

    /* ================= 訊息管理 ================= */

    /** ✔ 儲存訊息 */
    public void saveMessage(Integer roomId, ChatMessageDTO message) {
        // ✔ 儲存訊息到 List
        redisStr.opsForList().rightPush(ChatRedisKey.chatMessages(roomId), gson.toJson(message));

        // ✔ 增加對方的未讀數
        Integer receiverUserId = (message.getSenderType() == 0)
                ? getEmployeeId(roomId)  // 會員傳給客服
                : getMemberId(roomId);   // 客服傳給會員

        redisStr.opsForValue().increment(ChatRedisKey.chatUnread(roomId, receiverUserId));
    }

    /** ✔ 查詢歷史訊息 */
    public List<ChatMessageDTO> getMessageHistory(Integer roomId) {
        List<String> list = redisStr.opsForList().range(ChatRedisKey.chatMessages(roomId), 0, -1);
        if (list == null) return new ArrayList<>();

        return list.stream()
                .map(json -> gson.fromJson(json, ChatMessageDTO.class))
                .collect(Collectors.toList());
    }

    /* ================= 未讀管理 ================= */

    /** ✔ 查詢未讀 */
    public Integer getUnreadCount(Integer roomId, Integer userId) {
        String count = redisStr.opsForValue().get(ChatRedisKey.chatUnread(roomId, userId));
        return count == null ? 0 : Integer.parseInt(count);
    }

    /** ✔ 清除未讀（進入聊天室時） */
    public void clearUnread(Integer roomId, Integer userId) {
        redisStr.delete(ChatRedisKey.chatUnread(roomId, userId));
    }

    /* ================= 工具 ================= */

    /** ✔ 查詢會員ID */
    public Integer getMemberId(Integer roomId) {
        ChatRoomDTO room = getChatRoom(roomId);
        return room == null ? null : room.getMemberId();
    }

    /** ✔ 查詢客服ID */
    public Integer getEmployeeId(Integer roomId) {
        ChatRoomDTO room = getChatRoom(roomId);
        return room == null ? null : room.getEmployeeId();
    }
    /** ✔ 更新聊天室資訊 */
    public void saveChatRoom(ChatRoomDTO room) {
        redisStr.opsForValue().set(ChatRedisKey.chatRoom(room.getChatRoomId()), gson.toJson(room));
    }
    /** ✔ 增加未讀 */
    public void incrementUnread(Integer roomId, Integer userId) {
        redisStr.opsForValue().increment(ChatRedisKey.chatUnread(roomId, userId));
    }
}
