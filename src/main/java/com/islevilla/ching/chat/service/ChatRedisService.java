package com.islevilla.ching.chat.service;

import java.util.ArrayList;
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
import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoomResult;

@Service
public class ChatRedisService {

	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate<String, String> redisStr;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ChatRoomUpdateService chatRoomUpdateService;

	private final Gson gson = new Gson();

	// 建立聊天室（不存在就建立）
	public ChatRoomResult getOrCreateChatRoom(Integer memberId, String memberName, Integer employeeId,
			String employeeName) {
		Integer existingRoomId = getChatRoomIdByMember(memberId);
		if (existingRoomId != null) {
			return new ChatRoomResult(getChatRoom(existingRoomId), false);
		}

		int roomId = chatRoomUpdateService.incrementChatRoomId();
		ChatRoomDTO room = ChatRoomDTO.builder().chatRoomId(roomId).memberId(memberId).memberName(memberName)
				.employeeId(employeeId).employeeName(employeeName).chatStatus(1).unreadCount(0).build();

		chatRoomUpdateService.saveChatRoom(room);
		chatRoomUpdateService.bindMemberToRoom(memberId, roomId);
		return new ChatRoomResult(room, true);
	}

	// 查詢單一聊天室
	public ChatRoomDTO getChatRoom(Integer roomId) {
		String json = redisStr.opsForValue().get(ChatRedisKey.chatRoom(roomId));
		if (json == null)
			return null;

		ChatRoomDTO room = gson.fromJson(json, ChatRoomDTO.class);

		// 查資料庫補充名稱
		room.setMemberName(getMemberName(room.getMemberId()));
		room.setEmployeeName(getEmployeeName(room.getEmployeeId()));

		// 撈最後訊息時間
		room.setLastMessageTime(chatRoomUpdateService.getLastMessageTime(roomId));
		return room;
	}

	// 查詢所有聊天室
	public List<ChatRoomDTO> getAllChatRooms() {
		Set<String> ids = redisStr.opsForSet().members(ChatRedisKey.CHAT_ROOMS_SET);
		if (ids == null || ids.isEmpty())
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

	
	
	/* ================= 訊息管理 ================= */

	// 儲存訊息
	public void saveMessage(Integer roomId, ChatMessageDTO message) {
		redisStr.opsForList().rightPush(ChatRedisKey.chatMessages(roomId), gson.toJson(message));
		chatRoomUpdateService.updateLastMessageTime(roomId, message.getMessageTime());

		if (message.getSenderType() == 0)
			// 會員發送 會增加客服的未讀
			redisStr.opsForValue().increment(ChatRedisKey.chatUnreadEmployee(roomId));
		else
			// 客服發送 會增加會員的未讀
			redisStr.opsForValue().increment(ChatRedisKey.chatUnreadMember(roomId));
	}

	// 查詢歷史訊息
	public List<ChatMessageDTO> getMessageHistory(Integer roomId, boolean afterEnd) {
		List<String> list = redisStr.opsForList().range(ChatRedisKey.chatMessages(roomId), 0, -1);
		if (list == null)
			return new ArrayList<>();

		if (!afterEnd) {
			return list.stream().map(s -> gson.fromJson(s, ChatMessageDTO.class)).collect(Collectors.toList());
		}
		Long endTime = chatRoomUpdateService.getRoomEndTime(roomId);
		return list.stream().map(s -> gson.fromJson(s, ChatMessageDTO.class))
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

	// 客服未讀（共用
	public Integer getUnreadCountForEmployee(Integer roomId) {
		String count = redisStr.opsForValue().get(ChatRedisKey.chatUnreadEmployee(roomId));
		return count == null ? 0 : Integer.parseInt(count);
	}

	// 依會員抓房間ID
	public Integer getChatRoomIdByMember(Integer memberId) {
		String id = redisStr.opsForValue().get(ChatRedisKey.chatMemberRoom(memberId));
		return id == null ? null : Integer.parseInt(id);
	}
	
	// 抓會員ID
	public Integer getMemberId(Integer roomId) {
		ChatRoomDTO room = getChatRoom(roomId);
		return room == null ? null : room.getMemberId();
	}

	// 抓管理員ID
	public Integer getEmployeeId(Integer roomId) {
		ChatRoomDTO room = getChatRoom(roomId);
		return room == null ? null : room.getEmployeeId();
	}

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
}