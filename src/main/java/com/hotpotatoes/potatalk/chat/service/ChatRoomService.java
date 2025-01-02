package com.hotpotatoes.potatalk.chat.service;

import com.hotpotatoes.potatalk.chat.domain.ChatRoomStatus;
import com.hotpotatoes.potatalk.chat.dto.ChatRoomResponseDto;
import com.hotpotatoes.potatalk.chat.domain.ChatRoom;
import com.hotpotatoes.potatalk.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public ChatRoomStatus getChatRoomStatus(int chatId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatId);
        if (chatRoom.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }
        return chatRoom.get().getStatus();
    }

    public void updateChatRoomStatus(int chatId, boolean accepted) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);
        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        if (accepted) {
            chatRoom.setStatus(ChatRoomStatus.IN_CHAT);
        } else {
            chatRoom.setStatus(ChatRoomStatus.WAITING);
        }

        chatRoomRepository.save(chatRoom);
    }

}
