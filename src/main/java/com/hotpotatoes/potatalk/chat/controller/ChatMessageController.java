package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.dto.ChatMessageDto;
import com.hotpotatoes.potatalk.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/message")
    public void handleChatMessage(ChatMessageDto message) {
        // 메시지를 해당 채팅방으로 전달
        messagingTemplate.convertAndSend("/topic/chat/" + message.getChatId(), message);
    }

    @DeleteMapping("/messages/{message-id}")
    public ResponseEntity<Void> deleteChatMessage(@PathVariable("message-id") int messageId) {
        chatMessageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

}
