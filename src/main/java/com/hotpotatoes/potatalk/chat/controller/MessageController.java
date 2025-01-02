package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/message")
    public void handleChatMessage(ChatMessageDto message) {
        // 메시지를 해당 채팅방으로 전달
        messagingTemplate.convertAndSend("/topic/chat/" + message.getChatId(), message);
    }
}
