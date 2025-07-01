package com.islevilla.ching.chat.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;

@Service
public class ChatRoomQueryService {

    @Autowired
    @Qualifier("redisStringTemplate")
    private RedisTemplate<String, String> redisStr;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final Gson gson = new Gson();
    
    /** ✔ 從資料庫查會員名稱 */
    private String getMemberName(Integer memberId) {
        String sql = "SELECT member_name FROM member WHERE member_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, memberId);
        } catch (Exception e) {
            return "未知會員";
        }
    }
    
    /** ✔ 從資料庫查客服名稱 */
    private String getEmployeeName(Integer employeeId) {
        String sql = "SELECT employee_name FROM employee WHERE employee_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, employeeId);
        } catch (Exception e) {
            return "未知客服";
        }
    }

    /** ✔ 查詢所有聊天室 + 未讀數 + 名稱 */
    public List<ChatRoomDTO> getAllChatRoomsWithUnread(Integer userId) {
        Set<String> roomIds = redisStr.opsForSet().members(ChatRedisKey.CHAT_ROOMS_SET);
        if (roomIds == null) return Collections.emptyList();

        return roomIds.stream().map(id -> {
            Integer roomId = Integer.parseInt(id);
            String json = redisStr.opsForValue().get(ChatRedisKey.chatRoom(roomId));
            if (json == null) return null;

            ChatRoomDTO room = gson.fromJson(json, ChatRoomDTO.class);

            // ✔ 查MySQL補充名稱
            room.setMemberName(getMemberName(room.getMemberId()));
            room.setEmployeeName(getEmployeeName(room.getEmployeeId()));

            // ✔ 查Redis補未讀
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

        // ✔ 查MySQL補充名稱
        room.setMemberName(getMemberName(room.getMemberId()));
        room.setEmployeeName(getEmployeeName(room.getEmployeeId()));

        // ✔ 查Redis補未讀
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
