package com.hotpotatoes.potatalk.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {
    private int chatId;       // 채팅방 ID
    private String sender;    // 메시지 발신자
    private String content;   // 메시지 내용
}
