package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.dto.ChatMessageDto;
import com.hotpotatoes.potatalk.chat.dto.MarkMessagesAsReadDto;
import com.hotpotatoes.potatalk.chat.dto.MessageDeleteDto;
import com.hotpotatoes.potatalk.chat.service.ChatMediaService;
import com.hotpotatoes.potatalk.chat.service.ChatMessageService;
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

    @MessageMapping("/{chatId}/messages")
    public void handleChatMessage(ChatMessageDto message) {
        chatMessageService.saveMessage(message.getChatId(), message);

        // WebSocket으로 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + message.getChatId(), message);
    }

    @MessageMapping("/messages/read")
    public void markMessagesAsRead(MarkMessagesAsReadDto markMessagesAsReadDto) {
        int chatId = markMessagesAsReadDto.getChatId();

        chatMessageService.readMessage(chatId);

        // 메시지가 읽음 처리된 이벤트를 클라이언트로 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/read", "채팅방 " + chatId + "의 모든 메시지가 읽음 처리되었습니다.");
    }


    @MessageMapping("/message/delete")
    public void deleteChatMessage(MessageDeleteDto deleteDto) {
        int messageId = deleteDto.getMessageId();  // deleteDto를 통해 messageId를 받음

        chatMessageService.deleteMessage(messageId);  // 메시지 삭제

        // 삭제된 메시지 정보를 클라이언트로 전송
        String responseMessage = "메시지 " + messageId + "가 삭제되었습니다.";
        messagingTemplate.convertAndSend("/topic/chat/delete", responseMessage);
    }

    @PostMapping("/{chatId}/upload/photo")
    public ResponseEntity<String> uploadPhoto(@PathVariable("chatId") int chatId, @RequestParam("file") MultipartFile file) {
        try {
            String photoUrl = chatMediaService.savePhoto(chatId, file);
            // 사진 업로드 후 WebSocket 알림 전송
            messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/photo", photoUrl);
            return ResponseEntity.ok("사진이 성공적으로 전송되었습니다.");
        } catch (IOException e) {
            // 로깅
            e.printStackTrace();

            // 클라이언트에게 적절한 오류 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사진 전송에 실패하였습니다. : " + e.getMessage());
        }
    }

    @PostMapping("/{chatId}/upload/video")
    public ResponseEntity<String> uploadVideo(@PathVariable("chatId") int chatId, @RequestParam("file") MultipartFile file) {
        try {
            String videoUrl = chatMediaService.saveVideo(chatId, file);
            // 영상 업로드 후 WebSocket 알림 전송
            messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/video", videoUrl);
            return ResponseEntity.ok("비디오가 성공적으로 전송되었습니다.");
        } catch (IOException e) {
            // 로깅
            e.printStackTrace();

            // 클라이언트에게 적절한 오류 메시지 반환
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
