package com.islevilla.ching.chat.modelDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDTO {
    private Integer chatRoomId;
    private Integer memberId;
    private String memberName;
    private Integer employeeId;
    private String employeeName;
    private Integer chatStatus; // 1 = 進行中，0 = 已結束
    private Integer unreadCount; // ✔ 未讀訊息數
}