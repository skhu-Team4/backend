package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.domain.ChatRoomStatus;
import com.hotpotatoes.potatalk.chat.dto.ChatMessageDto;
import com.hotpotatoes.potatalk.chat.dto.ChatRoomConnectRequestDto;
import com.hotpotatoes.potatalk.chat.dto.ChatRoomResponseDto;
import com.hotpotatoes.potatalk.chat.domain.ChatRoom;
import com.hotpotatoes.potatalk.chat.service.ChatRoomService;
import com.hotpotatoes.potatalk.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/create")
    public void createChatRoom(String userId) {
        ChatRoomResponseDto chatRoom = chatRoomService.createChatRoom();

        // 클라이언트로 생성된 채팅방 정보 전송
        messagingTemplate.convertAndSend("/topic/chat/create/" + userId, chatRoom);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoom>> getAllChatRooms() {
        List<ChatRoom> chatRooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/{chatId}/status")
    public ChatRoomStatus getChatRoomStatus(@PathVariable("chatId") int chatId) {
        return chatRoomService.getChatRoomStatus(chatId);
    }

    @MessageMapping("/chat/status")
    public void updateChatRoomStatus(int chatId, boolean accepted) {
        chatRoomService.updateChatRoomStatus(chatId, accepted);

        // 채팅방 상태 변경 알림 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/status",
                accepted ? "채팅방 상태가 '채팅 중'으로 변경되었습니다." : "채팅방 상태가 '대기 중'으로 변경되었습니다.");
    }

    @MessageMapping("/chat/connect")
    public void connectToChatRoom(ChatRoomConnectRequestDto connectRequestDto) {
        String response = chatRoomService.connectToChatRoom(connectRequestDto.getChatId(), connectRequestDto.getUserId());

        // 연결 상태를 클라이언트로 전송
        messagingTemplate.convertAndSend("/topic/chat/connect/" + connectRequestDto.getChatId(), response);
    }

    // REST API로 메시지 전송 및 저장
    @PostMapping("/{chatId}/messages")
    public void sendMessageToChatRoom(
            @PathVariable("chatId") int chatId,
            @RequestBody ChatMessageDto messageDto
    ) {
        // 메시지를 저장하고 WebSocket으로 전송
        chatMessageService.saveMessage(chatId, messageDto);
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, messageDto);
    }
}
