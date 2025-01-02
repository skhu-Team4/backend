package com.hotpotatoes.potatalk.chat.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    private String messageContent;
    private int chatRoomId;
    private LocalDateTime createdAt = LocalDateTime.now();
}
