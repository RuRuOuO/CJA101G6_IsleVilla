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
@Table(name = "chat_room")
@Data
public class ChatRoom {

    @Id
    @Column(name = "chat_room_id")
    private Integer chatRoomId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "chat_status", nullable = false)
    private Integer chatStatus;  // 1=進行中, 0=已結束

}
