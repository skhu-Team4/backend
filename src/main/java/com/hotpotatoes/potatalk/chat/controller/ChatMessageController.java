package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.dto.ChatMessageDto;
import com.hotpotatoes.potatalk.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/message")
    public void handleChatMessage(ChatMessageDto message) {
        chatMessageService.saveMessage(message.getChatId(), message);

        // WebSocket으로 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + message.getChatId(), message);
    }

    @MessageMapping("/messages/read")
    public void markMessagesAsRead(int chatId) {
        chatMessageService.readMessage(chatId);

        // 메시지가 읽음 처리된 이벤트를 클라이언트로 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/read", "Messages marked as read");
    }

    @MessageMapping("/message/delete")
    public void deleteChatMessage(int messageId) {
        chatMessageService.deleteMessage(messageId);

        // 삭제된 메시지 정보를 클라이언트로 전송
        messagingTemplate.convertAndSend("/topic/chat/delete", "Message " + messageId + " deleted");
    }
}
