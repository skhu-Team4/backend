package com.hotpotatoes.potatalk.chat.dto;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    public enum MessageType{
        ENTER, TALK
    }

    private MessageType messageType;
    private Long chatRoomId;
    private Long senderId;
    private String message;
}
