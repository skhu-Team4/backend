package com.hotpotatoes.potatalk.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatRoomResponseDto {
    private int chatId;
    private LocalDateTime createdAt;
}
