
package com.islevilla.ching.chat.modelDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatRoomDTO {
	private Integer memberId;
    private Integer chatRoomId;
    private String memberName;
    private Integer employeeId;
    private String employeeName;
    private Integer chatStatus; // 1 = 進行中 2 = 已結案
    private Integer unreadCount; // ✔ 未讀訊息數
    private Long lastMessageTime;
	
}
