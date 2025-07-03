package com.islevilla.ching.chat.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.islevilla.ching.chat.modelDTO.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer>{

	List<ChatMessage> findByChatRoomId(Integer chatroomId);
}
