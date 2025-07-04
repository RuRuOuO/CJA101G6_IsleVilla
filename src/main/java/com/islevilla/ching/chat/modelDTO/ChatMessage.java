package com.islevilla.ching.chat.modelDTO;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "chat_message")
@Data
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    @Column(name = "chat_room_id", nullable = false)
    private Integer chatRoomId;

    @Column(name = "sender_type", nullable = false)
    private Integer senderType; // 0=會員, 1=客服

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "message_content", nullable = false)
    private String messageContent;

    @Column(name = "is_read", nullable = false)
    private Integer isRead; // 0=未讀, 1=已讀

    @Column(name = "message_time", nullable = false)
    private Timestamp messageTime;
}
