
package com.islevilla.ching.chat.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;

@Service
public class ChatRoomUpdateService {

	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate<String, String> redisStr;

	private final Gson gson = new Gson();

	@Autowired
	private ChatRoomQueryService chatRoomQueryService;

	@Autowired
	@Lazy
	private ChatRedisService chatRedisService;

	// 儲存聊天室
	public void saveChatRoom(ChatRoomDTO room) {
		String roomIdStr = String.valueOf(room.getChatRoomId());
		redisStr.opsForValue().set(ChatRedisKey.chatRoom(room.getChatRoomId()), gson.toJson(room));

		// 自動補進聊天室清單集合
		redisStr.opsForSet().add(ChatRedisKey.CHAT_ROOMS_SET, roomIdStr);
	}

	// 刪除聊天室
	public void deleteChatRoom(Integer roomId) {
		ChatRoomDTO room = chatRoomQueryService.getChatRoom(roomId, null); // 若只查資料，不用 userId 補未讀

		redisStr.delete(ChatRedisKey.chatRoom(roomId));
		redisStr.delete(ChatRedisKey.chatMessages(roomId));
		redisStr.delete(ChatRedisKey.chatUnreadEmployee(roomId));
		redisStr.delete(ChatRedisKey.chatUnreadMember(roomId));
		redisStr.delete(ChatRedisKey.chatLastMessageTime(roomId));
		redisStr.delete(ChatRedisKey.chatRoomEndTime(roomId));

		if (room != null) {
			redisStr.delete(ChatRedisKey.chatMemberRoom(room.getMemberId()));
		}

		redisStr.opsForSet().remove(ChatRedisKey.CHAT_ROOMS_SET, String.valueOf(roomId));
	}

	// 自增聊天室ID
	public Integer incrementChatRoomId() {
		return Optional.ofNullable(redisStr.opsForValue().increment(ChatRedisKey.chatRoomSeq())).orElse(1L).intValue();
	}

	// 綁定會員對應的聊天室ID
	public void bindMemberToRoom(Integer memberId, Integer roomId) {
		redisStr.opsForValue().set(ChatRedisKey.chatMemberRoom(memberId), String.valueOf(roomId));
	}

	// 解除會員與聊天室綁定（例如客服點結束聊天室）
	public void unbindMemberFromRoom(Integer memberId) {
		redisStr.delete(ChatRedisKey.chatMemberRoom(memberId));
	}

	// 更新結案時間
	public void markRoomEndTime(Integer roomId) {
		redisStr.opsForValue().set(ChatRedisKey.chatRoomEndTime(roomId), String.valueOf(System.currentTimeMillis()));
	}

	// 查詢結案時間
	public Long getRoomEndTime(Integer roomId) {
		String value = redisStr.opsForValue().get(ChatRedisKey.chatRoomEndTime(roomId));
		return (value != null) ? Long.parseLong(value) : null;
	}

	// 查詢最後訊息時間
	public Long getLastMessageTime(Integer roomId) {
		String value = redisStr.opsForValue().get(ChatRedisKey.chatLastMessageTime(roomId));
		return (value != null) ? Long.parseLong(value) : null;
	}

	// 更新最後訊息時間
	public void updateLastMessageTime(Integer roomId, Long timestamp) {
		redisStr.opsForValue().set(ChatRedisKey.chatLastMessageTime(roomId), String.valueOf(timestamp));
	}

	// 查詢聊天室狀態
	public Integer getChatRoomStatus(Integer roomId) {
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		return room != null ? room.getChatStatus() : 0;
	}

	// 聊天室重新開啟（狀態=1）
	public void reopenRoom(Integer roomId) {
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room != null) {
			room.setChatStatus(1);
			saveChatRoom(room);
		}
	}

	// 聊天室結案（狀態=2）
	public void completeRoom(Integer roomId) {
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room != null) {
			room.setChatStatus(2);
			saveChatRoom(room);
			markRoomEndTime(roomId);
		}
	}

	// 聊天室等待中（狀態=3）
	public void markRoomPending(Integer roomId) {
		ChatRoomDTO room = chatRedisService.getChatRoom(roomId);
		if (room != null && room.getChatStatus() == 2) {
			room.setChatStatus(3); // 等待回覆
			saveChatRoom(room);
		}
	}

	// 清除會員未讀（進入聊天室時）
	public void clearUnreadForMember(Integer roomId) {
		redisStr.delete(ChatRedisKey.chatUnreadMember(roomId));
	}

	// 清除客服未讀
	public void clearUnreadForEmployee(Integer roomId) {
		redisStr.delete(ChatRedisKey.chatUnreadEmployee(roomId));
	}

	// 結束聊天室
	public void closeChatRoom(Integer roomId) {
		ChatRoomDTO room = chatRoomQueryService.getChatRoom(roomId, null);
		if (room != null) {
			room.setChatStatus(0);
			saveChatRoom(room);
		}
	}
}
