package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.domain.NotificationSettings;
import com.hotpotatoes.potatalk.chat.dto.message.req.ChatMessageDto;
import com.hotpotatoes.potatalk.chat.dto.match.req.MarkMessagesAsReadDto;
import com.hotpotatoes.potatalk.chat.dto.message.req.MessageDeleteDto;
import com.hotpotatoes.potatalk.chat.service.ChatMediaService;
import com.hotpotatoes.potatalk.chat.service.ChatMessageService;
import com.hotpotatoes.potatalk.chat.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatMediaService chatMediaService;
    private final NotificationService notificationService;

    @MessageMapping("/{chatId}/messages")
    public void handleChatMessage(ChatMessageDto message) {
        chatMessageService.saveMessage(message.getChatId(), message);

        messagingTemplate.convertAndSend("/topic/chat/" + message.getChatId(), message);

        NotificationSettings userSettings = notificationService.getNotificationSettings(message.getSender());

        if (userSettings != null && userSettings.isMessageNotificationEnabled()) {
            notificationService.saveNotification(message.getSender(), "새 메시지가 도착했습니다.");
        }
    }

    @MessageMapping("/messages/read")
    public void markMessagesAsRead(MarkMessagesAsReadDto markMessagesAsReadDto) {
        int chatId = markMessagesAsReadDto.getChatId();

        chatMessageService.readMessage(chatId);

        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/read", "채팅방 " + chatId + "의 모든 메시지가 읽음 처리되었습니다.");
    }


    @MessageMapping("/message/delete")
    public void deleteChatMessage(MessageDeleteDto deleteDto) {
        int messageId = deleteDto.getMessageId();

        chatMessageService.deleteMessage(messageId);

        String responseMessage = "메시지 " + messageId + "가 삭제되었습니다.";
        messagingTemplate.convertAndSend("/topic/chat/delete", responseMessage);
    }

    @PostMapping("/{chatId}/upload/photo")
    public ResponseEntity<String> uploadPhoto(@PathVariable("chatId") int chatId, @RequestParam("file") MultipartFile file) {
        try {
            String photoUrl = chatMediaService.savePhoto(chatId, file);

            messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/photo", photoUrl);
            return ResponseEntity.ok("사진이 성공적으로 전송되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사진 전송에 실패하였습니다. : " + e.getMessage());
        }
    }

    @PostMapping("/{chatId}/upload/video")
    public ResponseEntity<String> uploadVideo(@PathVariable("chatId") int chatId, @RequestParam("file") MultipartFile file) {
        try {
            String videoUrl = chatMediaService.saveVideo(chatId, file);

            messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/video", videoUrl);
            return ResponseEntity.ok("비디오가 성공적으로 전송되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("비디오 전송에 실패하였습니다. : " + e.getMessage());
        }
    }

    @GetMapping("/{chatId}/unread/messages/count")
    public ResponseEntity<Long> getUnreadMessageCount(@PathVariable("chatId") int chatId) {
        try {
            long unreadCount = chatMessageService.getUnreadMessageCount(chatId);
            return ResponseEntity.ok(unreadCount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0L);
        }
    }
}
