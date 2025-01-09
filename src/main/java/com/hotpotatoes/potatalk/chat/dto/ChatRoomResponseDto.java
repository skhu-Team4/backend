package com.hotpotatoes.potatalk.chat.dto;

import com.hotpotatoes.potatalk.chat.domain.ChatRoomStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatRoomResponseDto {
    private int chatId;
    private ChatRoomStatus status;
    private LocalDateTime createdAt;
}
