package com.islevilla.ching.chat.service;

import com.google.gson.Gson;
import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatRedisService {

	@Autowired
	@Qualifier("redisStringTemplatedDb1") // 用來存未讀數或簡單ID
	private RedisTemplate<String, String> redisStr;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private final Gson gson = new Gson();

	// 建立聊天室（不存在就建立）
	public ChatRoomDTO getOrCreateChatRoom(Integer memberId, String memberName, Integer employeeId,
			String employeeName) {
		Integer existingRoomId = getChatRoomIdByMember(memberId);
		if (existingRoomId != null) {
			return getChatRoom(existingRoomId);
		}

		int roomId = incrementChatRoomId();
		ChatRoomDTO room = ChatRoomDTO.builder().chatRoomId(roomId).memberId(memberId).memberName(memberName)
				.employeeId(employeeId).employeeName(employeeName).chatStatus(1).unreadCount(0).build();

		saveChatRoom(room);
		redisStr.opsForSet().add(ChatRedisKey.CHAT_ROOMS_SET, String.valueOf(roomId));
		bindMemberToRoom(memberId, roomId);
		return room;
	}

	// 查詢單一聊天室（即時補名稱）
	public ChatRoomDTO getChatRoom(Integer roomId) {
		String json = redisStr.opsForValue().get(ChatRedisKey.chatRoom(roomId));
		if (json == null)
			return null;

		ChatRoomDTO room = gson.fromJson(json, ChatRoomDTO.class);

		// 查資料庫補充名稱
		room.setMemberName(getMemberName(room.getMemberId()));
		room.setEmployeeName(getEmployeeName(room.getEmployeeId()));

		// 撈最後訊息時間
		Long lastMsgTime = getLastMessageTime(roomId);
		room.setLastMessageTime(lastMsgTime != null ? lastMsgTime : 0L);

		return room;
	}

	// 查詢所有聊天室
	public List<ChatRoomDTO> getAllChatRooms() {
		Set<String> ids = redisStr.opsForSet().members(ChatRedisKey.CHAT_ROOMS_SET);
		if (ids == null)
			return Collections.emptyList();

		return ids.stream().map(id -> getChatRoom(Integer.parseInt(id))).filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	// 查詢所有聊天室（帶客服未讀）
	public List<ChatRoomDTO> getAllChatRoomsWithEmployeeUnread() {
		return getAllChatRooms().stream().map(room -> {
			int unread = getUnreadCountForEmployee(room.getChatRoomId());
			room.setUnreadCount(unread);
			return room;
		}).collect(Collectors.toList());
	}

	// 更新聊天室資訊
	public void saveChatRoom(ChatRoomDTO room) {
		redisStr.opsForValue().set(ChatRedisKey.chatRoom(room.getChatRoomId()), gson.toJson(room));
	}

	// 聊天室結案（狀態=2）
	public void completeRoom(Integer roomId) {
		ChatRoomDTO room = getChatRoom(roomId);
		if (room != null) {
			room.setChatStatus(2);
			saveChatRoom(room);
		}
	}

	// 聊天室重新開啟（狀態=1）
	public void reopenRoom(Integer roomId) {
		ChatRoomDTO room = getChatRoom(roomId);
		if (room != null) {
			room.setChatStatus(1);
			saveChatRoom(room);
		}
	}

	// 結束聊天室
	public void closeChatRoom(Integer roomId) {
		ChatRoomDTO room = getChatRoom(roomId);
		if (room != null) {
			room.setChatStatus(0); // 0 = 結束
			saveChatRoom(room);
		}
	}

	// 查詢聊天室狀態
	public Integer getChatRoomStatus(Integer roomId) {
		ChatRoomDTO room = getChatRoom(roomId);
		return room != null ? room.getChatStatus() : 0;
	}

	// 刪除聊天室（含訊息與未讀）
	public void deleteChatRoom(Integer roomId) {
		ChatRoomDTO room = getChatRoom(roomId);

		redisStr.delete(ChatRedisKey.chatRoom(roomId));
		redisStr.delete(ChatRedisKey.chatMessages(roomId));
		redisStr.delete(ChatRedisKey.chatUnreadEmployee(roomId));
		redisStr.delete(ChatRedisKey.chatUnreadMember(roomId));

		if (room != null) {
			redisStr.delete(ChatRedisKey.chatMemberRoom(room.getMemberId()));
		}

		redisStr.opsForSet().remove(ChatRedisKey.CHAT_ROOMS_SET, String.valueOf(roomId));
	}

	// 自增聊天室ID
	public Integer incrementChatRoomId() {
		return Optional.ofNullable(redisStr.opsForValue().increment(ChatRedisKey.chatRoomSeq())).orElse(1L).intValue();
	}

	/* ================= 訊息管理 ================= */

	// 儲存訊息
	public void saveMessage(Integer roomId, ChatMessageDTO message) {
		redisStr.opsForList().rightPush(ChatRedisKey.chatMessages(roomId), gson.toJson(message));

		if (message.getSenderType() == 0) {
			// 會員發送 → 增加客服的未讀
			redisStr.opsForValue().increment(ChatRedisKey.chatUnreadEmployee(roomId));
		} else {
			// 客服發送 → 增加會員的未讀
			redisStr.opsForValue().increment(ChatRedisKey.chatUnreadMember(roomId));
		}
	}

	// 查詢歷史訊息
	public List<ChatMessageDTO> getMessageHistory(Integer roomId, boolean afterEnd) {
		List<String> list = redisStr.opsForList().range(ChatRedisKey.chatMessages(roomId), 0, -1);
		if (list == null)
			return new ArrayList<>();

		 if (!afterEnd) {
		        return list.stream()
		                .map(s -> gson.fromJson(s, ChatMessageDTO.class))
		                .collect(Collectors.toList());
		    }
		 Long endTime = getRoomEndTime(roomId);
		    if (endTime == null) {
		        // 沒有結案時間，回傳全部
		        return list.stream()
		                .map(s -> gson.fromJson(s, ChatMessageDTO.class))
		                .collect(Collectors.toList());
		    }
		    return list.stream()
		            .map(s -> gson.fromJson(s, ChatMessageDTO.class))
		            .filter(msg -> msg.getMessageTime() != null && msg.getMessageTime() > endTime)
		            .collect(Collectors.toList());
		}
	
	// 預設查詢全部訊息
	public List<ChatMessageDTO> getMessageHistory(Integer roomId) {
	    return getMessageHistory(roomId, false); 
	}

	/* ================= 未讀管理 ================= */

	// 查詢會員未讀
	public Integer getUnreadCountForMember(Integer roomId) {
		String count = redisStr.opsForValue().get(ChatRedisKey.chatUnreadMember(roomId));
		return count == null ? 0 : Integer.parseInt(count);
	}

	// 客服未讀（共用）
	public Integer getUnreadCountForEmployee(Integer roomId) {
		String count = redisStr.opsForValue().get(ChatRedisKey.chatUnreadEmployee(roomId));
		return count == null ? 0 : Integer.parseInt(count);
	}

	// 清除會員未讀（進入聊天室時）
	public void clearUnreadForMember(Integer roomId) {
		redisStr.delete(ChatRedisKey.chatUnreadMember(roomId));
	}

	// 清除客服未讀
	public void clearUnreadForEmployee(Integer roomId) {
		redisStr.delete(ChatRedisKey.chatUnreadEmployee(roomId));
	}

	/* ================= 工具 ================= */

	// 從資料庫查會員名稱
	private String getMemberName(Integer memberId) {
		try {
			return jdbcTemplate.queryForObject("SELECT member_name FROM members WHERE member_id = ?", String.class,
					memberId);
		} catch (Exception e) {
			return "未知會員";
		}
	}

	// 從資料庫查客服名稱
	private String getEmployeeName(Integer employeeId) {
		try {
			return jdbcTemplate.queryForObject("SELECT employee_name FROM employee WHERE employee_id = ?", String.class,
					employeeId);
		} catch (Exception e) {
			return "未知客服";
		}
	}

	// 查聊天室對應的會員ID
	public Integer getMemberId(Integer roomId) {
		ChatRoomDTO room = getChatRoom(roomId);
		return room == null ? null : room.getMemberId();
	}

	// 查聊天室對應的客服ID
	public Integer getEmployeeId(Integer roomId) {
		ChatRoomDTO room = getChatRoom(roomId);
		return room == null ? null : room.getEmployeeId();
	}

	// 查詢會員對應的聊天室ID
	public Integer getChatRoomIdByMember(Integer memberId) {
		String id = redisStr.opsForValue().get(ChatRedisKey.chatMemberRoom(memberId));
		return id == null ? null : Integer.parseInt(id);
	}

	// 綁定會員對應的聊天室ID
	public void bindMemberToRoom(Integer memberId, Integer roomId) {
		redisStr.opsForValue().set(ChatRedisKey.chatMemberRoom(memberId), String.valueOf(roomId));
	}

	// 解除會員與聊天室綁定（例如客服點結束聊天室）
	public void unbindMemberFromRoom(Integer memberId) {
		redisStr.delete(ChatRedisKey.chatMemberRoom(memberId));
	}

	// 更新最後訊息時間
	public void updateLastMessageTime(Integer roomId, Long timestamp) {
		String key = "chatroom:lastMessageTime:" + roomId;
		redisStr.opsForValue().set(key, String.valueOf(timestamp));
	}

	// 查詢最後訊息時間
	public Long getLastMessageTime(Integer roomId) {
		String key = "chatroom:lastMessageTime:" + roomId;
		String value = redisStr.opsForValue().get(key);
		return value != null ? Long.parseLong(value) : null;
	}

	// 記錄結案時間
	public void markRoomEndTime(Integer roomId) {
		String key = "chat:endTime:" + roomId;
		redisStr.opsForValue().set(key, String.valueOf(System.currentTimeMillis()));
	}

	// 查詢結案時間
	public Long getRoomEndTime(Integer roomId) {
		String key = "chat:endTime:" + roomId;
		String value = redisStr.opsForValue().get(key);
		return (value != null) ? Long.parseLong(value) : null;
	}
}
