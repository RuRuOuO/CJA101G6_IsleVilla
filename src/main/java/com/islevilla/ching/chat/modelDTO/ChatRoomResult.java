package com.islevilla.ching.chat.modelDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoomResult {

	private ChatRoomDTO room;
    private boolean newlyCreated;
}

