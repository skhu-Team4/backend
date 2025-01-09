package com.hotpotatoes.potatalk.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomConnectRequestDto {
    private int chatId;
    private String userId;
}
