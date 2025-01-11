package com.hotpotatoes.potatalk.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {
    private int chatId;
    private String sender;
    private String content;
}
