package com.hotpotatoes.potatalk.chat.dto.chat.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomStatusUpdateDto {
    private int chatId;
    private boolean accepted;
}
