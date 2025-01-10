package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.dto.ChatMessageDto;
import com.hotpotatoes.potatalk.chat.service.ChatMediaService;
import com.hotpotatoes.potatalk.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatMediaService chatMediaService;

    @MessageMapping("/{chatId}/messages")
    public void handleChatMessage(ChatMessageDto message) {
        chatMessageService.saveMessage(message.getChatId(), message);

        // WebSocket으로 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + message.getChatId(), message);
    }

    @MessageMapping("/messages/read")
    public void markMessagesAsRead(int chatId) {
        chatMessageService.readMessage(chatId);

        // 메시지가 읽음 처리된 이벤트를 클라이언트로 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/read", "메시지가 읽음 처리되었습니다.");
    }

    @MessageMapping("/message/delete")
    public void deleteChatMessage(int messageId) {
        chatMessageService.deleteMessage(messageId);

        // 삭제된 메시지 정보를 클라이언트로 전송
        String responseMessage = "메시지 " + messageId + "가 삭제되었습니다.";
        messagingTemplate.convertAndSend("/topic/chat/delete", responseMessage);
    }

    @PostMapping("/{chatId}/upload/photo")
    public void uploadPhoto(@PathVariable("chatId") int chatId, @RequestBody MultipartFile file) {
        String photoUrl = chatMediaService.savePhoto(chatId, file);
        // 사진 업로드 후 WebSocket 알림 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/photo", photoUrl);
    }

    @PostMapping("/{chatId}/upload/video")
    public void uploadVideo(@PathVariable("chatId") int chatId, @RequestBody MultipartFile file) {
        String videoUrl = chatMediaService.saveVideo(chatId, file);
        // 영상 업로드 후 WebSocket 알림 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/video", videoUrl);
    }
}
