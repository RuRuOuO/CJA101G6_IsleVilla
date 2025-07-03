package com.islevilla.ching.chat.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.ching.chat.config.repository.ChatMessageRepository;
import com.islevilla.ching.chat.config.repository.ChatRoomRepository;
import com.islevilla.ching.chat.modelDTO.ChatMessage;
import com.islevilla.ching.chat.modelDTO.ChatMessageDTO;
import com.islevilla.ching.chat.modelDTO.ChatRoom;
import com.islevilla.ching.chat.modelDTO.ChatRoomDTO;

@Service
public class ChatRoomDbService {

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private ChatMessageRepository chatMessageRepository;

	// 新增聊天室
	public ChatRoom createChatRoom(ChatRoom room) {
		return chatRoomRepository.save(room);
	}

	// 新增訊息
	public ChatMessage saveMessage(ChatMessage message) {
		return chatMessageRepository.save(message);
	}

	// 聊天室與訊息匯入SQL
	@Transactional
	public void saveRoomAndMessageToSql(ChatRoomDTO roomDto, List<ChatMessageDTO> messageDtos) {

		// ChatRoomDTO實體轉換 先存聊天室
		ChatRoom c = new ChatRoom();
		c.setChatRoomId(roomDto.getChatRoomId());
		c.setMemberId(roomDto.getMemberId());
		c.setMemberName(roomDto.getMemberName());
		c.setEmployeeId(roomDto.getEmployeeId());
		c.setEmployeeName(roomDto.getEmployeeName());
		c.setChatStatus(roomDto.getChatStatus());

		chatRoomRepository.save(c);

		// 再儲存所有訊息
		List<ChatMessage> messages = messageDtos.stream().map(dto -> {

			ChatMessage m = new ChatMessage();
			m.setChatRoomId(dto.getChatRoomId());
			m.setSenderType(dto.getSenderType());
			m.setSenderId(dto.getSenderId());
			m.setSenderName(dto.getSenderName());
			m.setMessageContent(dto.getMessageContent());
			m.setIsRead(dto.getIsRead());
			
			// 時間從 毫秒(Long) 轉 Timestamp
			m.setMessageTime(new Timestamp(dto.getMessageTime()));
			return m; 
		}).toList();

		chatMessageRepository.saveAll(messages);
	}

	// 查聊天室 依員工ID
	public List<ChatRoom> getRoomByEmployeeId(Integer employeeId) {
		return chatRoomRepository.findByEmployeeId(employeeId);
	}

	// 查聊天室 依會員ID
	public List<ChatRoom> getRoomsByMemberId(Integer memberId) {
		return chatRoomRepository.findByMemberId(memberId);
	}

	// 查訊息 依聊天室ID
	public List<ChatMessage> getMessagesByRoomId(Integer roomId) {
		return chatMessageRepository.findByChatRoomId(roomId);
	}
}
