package com.islevilla.ching.chat.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.islevilla.ching.chat.modelDTO.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer>{

	List<ChatRoom> findByMemberId(Integer memberId);
	List<ChatRoom> findByEmployeeId(Integer employeeId);
}

