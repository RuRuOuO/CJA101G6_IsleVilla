package com.islevilla.ching.chat.service;

// Redis Key 統一管理

public final class ChatRedisKey {

	private static final String PREFIX = "chat:";

	// 聊天室集合（Set）
	public static final String CHAT_ROOMS_SET = PREFIX + "rooms";

	// 聊天室ID遞增序號
	public static String chatRoomSeq() {
		return PREFIX + "roomId:seq";
	}

	// 單一聊天室資訊
	public static String chatRoom(int roomId) {
		return String.format("%sroom:%d", PREFIX, roomId);
	}

	// 單一聊天室的訊息列表
	public static String chatMessages(int roomId) {
		return String.format("%smsg:%d", PREFIX, roomId);
	}

	// 單一聊天室對單一用戶的未讀數
	public static String chatUnread(int roomId, int userId) {
		return String.format("%sunread:%d:%d", PREFIX, roomId, userId);
	}

	// 單一聊天室針對客服的未讀數（多人共用）
	public static String chatUnreadEmployee(int roomId) {
		return String.format("%sunread:employee:%d", PREFIX, roomId);
	}

	// 單一聊天室針對會員的未讀數（獨立寫法，選擇性）
	public static String chatUnreadMember(int roomId) {
		return String.format("%sunread:member:%d", PREFIX, roomId);
	}

	// 綁定會員ID對應的聊天室ID
	public static String chatMemberRoom(int memberId) {
		return String.format("%smember:room:%d", PREFIX, memberId);
	}

}
