package com.islevilla.ching.chat.service;

import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatRoomQueryService {

    @Autowired
    @Qualifier("redisStringTemplate")
    private RedisTemplate<String, String> redisStr;

    private final Gson gson = new Gson();

    /** ✔ 查詢所有聊天室 + 未讀數 */
    public List<ChatRoomDTO> getAllChatRoomsWithUnread(Integer userId) {
        Set<String> roomIds = redisStr.opsForSet().members(ChatRedisKey.CHAT_ROOMS_SET);
        if (roomIds == null) return Collections.emptyList();

        return roomIds.stream().map(id -> {
            Integer roomId = Integer.parseInt(id);
            String json = redisStr.opsForValue().get(ChatRedisKey.chatRoom(roomId));
            if (json == null) return null;

            ChatRoomDTO room = gson.fromJson(json, ChatRoomDTO.class);
            Integer unread = getUnreadCount(roomId, userId);
            room.setUnreadCount(unread);

            return room;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /** ✔ 查詢單一聊天室 */
    public ChatRoomDTO getChatRoom(Integer roomId, Integer userId) {
        String json = redisStr.opsForValue().get(ChatRedisKey.chatRoom(roomId));
        if (json == null) return null;

        ChatRoomDTO room = gson.fromJson(json, ChatRoomDTO.class);
        Integer unread = getUnreadCount(roomId, userId);
        room.setUnreadCount(unread);

        return room;
    }

    /** ✔ 查詢未讀數 */
    public Integer getUnreadCount(Integer roomId, Integer userId) {
        String key = ChatRedisKey.chatUnread(roomId, userId);
        String count = redisStr.opsForValue().get(key);
        return count == null ? 0 : Integer.parseInt(count);
    }
}
