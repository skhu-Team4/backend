package com.hotpotatoes.potatalk.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomConnectRequestDto {
    private String userId; // 연결 요청 사용자 ID
}
