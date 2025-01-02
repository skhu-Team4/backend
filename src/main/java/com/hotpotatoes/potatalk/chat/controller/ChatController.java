package com.hotpotatoes.potatalk.chat.controller;

import com.hotpotatoes.potatalk.chat.domain.ChatRoomStatus;
import com.hotpotatoes.potatalk.chat.dto.ChatRoomResponseDto;
import com.hotpotatoes.potatalk.chat.domain.ChatRoom;
import com.hotpotatoes.potatalk.chat.dto.MatchResponseDto;
import com.hotpotatoes.potatalk.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomResponseDto> createChatRoom() {
        ChatRoomResponseDto response = chatRoomService.createChatRoom();
        return ResponseEntity.ok(response);
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

    @PutMapping("/{chatId}/status")
    public String updateChatRoomStatus(
            @PathVariable("chatId") int chatId,
            @RequestBody MatchResponseDto matchResponseDto
    ) {
        chatRoomService.updateChatRoomStatus(chatId, matchResponseDto.isAccepted());
        return matchResponseDto.isAccepted() ? "매칭이 수락되었습니다." : "매칭이 거절되었습니다.";
    }
}
