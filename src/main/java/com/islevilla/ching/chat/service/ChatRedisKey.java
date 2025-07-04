package com.islevilla.ching.chat.service;

// Redis Key 統一管理

public final class ChatRedisKey {

	private static final String PREFIX = "chat:";

	// 聊天室ID遞增序號
	public static String chatRoomSeq() {
		return PREFIX + "roomId:seq";
	}

	// 聊天室ID集合（Set）
	 public static final String CHAT_ROOMS_SET = PREFIX + "rooms:set";

	// 指定聊天室資料（JSON String
	public static String chatRoom(int roomId) {
		return PREFIX + "room:" + roomId;
	}

	// 指定聊天室訊息列表（List<String>）
	public static String chatMessages(int roomId) {
		return PREFIX + "msg:" + roomId;
	}

	// 客服端未讀數（整數)
	public static String chatUnreadEmployee(int roomId) {
		return PREFIX + "unread:employee:" + roomId;
	}

	// 會員端未讀數（整數）
	public static String chatUnreadMember(int roomId) {
		return PREFIX + "unread:member:" + roomId;
	}

	// 指定的未讀數
	public static String chatUnread(int roomId, int userId) {
		return PREFIX + "unread:" + roomId + ":" + userId;
	}

	// 會員與聊天室綁定關係
	public static String chatMemberRoom(int memberId) {
		return PREFIX + "member:room:" + memberId;
	}
	
	// 最後訊息時間戳（Long millis → String） 
    public static String chatLastMessageTime(int roomId) {
        return PREFIX + "room:lastMessageTime:" + roomId;
    }

    // 聊天室結案時間戳（Long millis → String) 
    public static String chatRoomEndTime(int roomId) {
        return PREFIX + "room:endTime:" + roomId;
    }
}
