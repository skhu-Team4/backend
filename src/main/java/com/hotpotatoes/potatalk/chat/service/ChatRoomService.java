package com.hotpotatoes.potatalk.chat.service;

import com.hotpotatoes.potatalk.chat.domain.ChatMessage;
import com.hotpotatoes.potatalk.chat.domain.ChatRoomStatus;
import com.hotpotatoes.potatalk.chat.dto.ChatMessageDto;
import com.hotpotatoes.potatalk.chat.dto.ChatRoomResponseDto;
import com.hotpotatoes.potatalk.chat.domain.ChatRoom;
import com.hotpotatoes.potatalk.chat.repository.ChatMessageRepository;
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
    private final ChatMessageRepository chatMessageRepository;

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

    public String connectToChatRoom(int chatId, String userId) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);

        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        // 사용자를 연결된 사용자 리스트에 추가
        if (chatRoom.getConnectedUsers().contains(userId)) {
            return "사용자 " + userId + "는 이미 채팅방에 연결되어 있습니다.";
        }

        chatRoom.getConnectedUsers().add(userId);

        // 상태를 IN_CHAT으로 변경
        if (chatRoom.getStatus() != null && chatRoom.getStatus() != ChatRoomStatus.IN_CHAT) {
            chatRoom.setStatus(ChatRoomStatus.IN_CHAT);
        }

        chatRoomRepository.save(chatRoom);

        return "사용자 " + userId + "가 채팅방 " + chatId + "에 연결되었습니다.";
    }

    public void saveMessage(int chatId, ChatMessageDto messageDto) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);

        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        // 메시지 저장
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageContent(messageDto.getContent());
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessage.setChatRoom(chatRoom);

        chatMessageRepository.save(chatMessage);
    }

}
