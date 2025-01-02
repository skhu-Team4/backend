package com.hotpotatoes.potatalk.chat.service;

import com.hotpotatoes.potatalk.chat.dto.ChatRoomResponseDto;
import com.hotpotatoes.potatalk.chat.entity.ChatRoom;
import com.hotpotatoes.potatalk.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomResponseDto createChatRoom() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setCreatedAt(LocalDateTime.now());

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        return new ChatRoomResponseDto(savedRoom.getChatId(), savedRoom.getCreatedAt());
    }

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }
}
