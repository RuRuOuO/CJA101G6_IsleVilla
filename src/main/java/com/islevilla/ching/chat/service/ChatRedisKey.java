package com.islevilla.ching.chat.service;

public final class ChatRedisKey {

    private static final String PREFIX = "chat:";

    public static final String CHAT_ROOMS_SET = PREFIX + "rooms";

    public static String chatRoom(int roomId) {
        return String.format("%sroom:%d", PREFIX, roomId);
    }

    public static String chatMessages(int roomId) {
        return String.format("%smsg:%d", PREFIX, roomId);
    }

    public static String chatRoomSeq() {
        return PREFIX + "roomId:seq";
    }

    public static String chatUnread(int roomId) {
        return String.format("%sunread:%d", PREFIX, roomId);
    }
}
