package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.domain.ChatRoomStatus;
import com.hotpotatoes.potatalk.chat.dto.chat.req.ChatRoomConnectRequestDto;
import com.hotpotatoes.potatalk.chat.dto.chat.res.ChatRoomResponseDto;
import com.hotpotatoes.potatalk.chat.domain.ChatRoom;
import com.hotpotatoes.potatalk.chat.dto.chat.req.ChatRoomStatusUpdateDto;
import com.hotpotatoes.potatalk.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/create")
    public void createChatRoom(String userId) {
        ChatRoomResponseDto chatRoom = chatRoomService.createChatRoom();
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

    @MessageMapping("/status")
    public void updateChatRoomStatus(ChatRoomStatusUpdateDto statusUpdateDto) {
        chatRoomService.updateChatRoomStatus(statusUpdateDto.getChatId(), statusUpdateDto.isAccepted());

        messagingTemplate.convertAndSend("/topic/chat/" + statusUpdateDto.getChatId() + "/status",
                statusUpdateDto.isAccepted() ? "채팅방 상태가 '채팅 중'으로 변경되었습니다." : "채팅방 상태가 '대기 중'으로 변경되었습니다.");
    }

    @MessageMapping("/connect")
    public void connectToChatRoom(ChatRoomConnectRequestDto connectRequestDto) {
        String response = chatRoomService.connectToChatRoom(connectRequestDto.getChatId(), connectRequestDto.getUserId());

        String message = "사용자 " + connectRequestDto.getUserId() + "가 채팅방 " + connectRequestDto.getChatId() + "에 연결되었습니다.";

        messagingTemplate.convertAndSend("/topic/chat/connect/" + connectRequestDto.getChatId(), message);
    }

    @MessageMapping("/disconnect")
    public void disconnectFromChatRoom(ChatRoomConnectRequestDto connectRequestDto) {
        String response = chatRoomService.disconnectFromChatRoom(connectRequestDto.getChatId(), connectRequestDto.getUserId());

        messagingTemplate.convertAndSend("/topic/chat/disconnect/" + connectRequestDto.getChatId(), response);
    }

}
