
package com.islevilla.ching.chat.modelDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
	private Integer messageId;        // ✔ 主鍵，訊息ID
    private Integer chatRoomId;
    private Integer senderType;       // 0 = 會員，1 = 客服
    private Integer senderId;
    private String senderName;
    private String messageContent;
    private Integer isRead;           // 0 = 未讀，1 = 已讀
    private Long messageTime;         // 時間戳 (epoch)
}
