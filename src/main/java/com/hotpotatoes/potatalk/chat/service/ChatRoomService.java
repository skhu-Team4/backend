package com.hotpotatoes.potatalk.chat.service;

import com.hotpotatoes.potatalk.chat.domain.ChatRoomStatus;
import com.hotpotatoes.potatalk.chat.dto.ChatRoomResponseDto;
import com.hotpotatoes.potatalk.chat.domain.ChatRoom;
import com.hotpotatoes.potatalk.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static com.hotpotatoes.potatalk.chat.domain.ChatRoomStatus.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomResponseDto createChatRoom() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setStatus(WAITING);
        chatRoom.setCreatedAt(LocalDateTime.now());

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        return new ChatRoomResponseDto(savedRoom.getChatId(), savedRoom.getStatus(), savedRoom.getCreatedAt());
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

    @Transactional
    public void updateChatRoomStatus(int chatId, boolean accepted) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);
        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        if (accepted) {
            chatRoom.setStatus(ChatRoomStatus.IN_CHAT);
        } else {
            chatRoom.setStatus(WAITING);
        }

        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public String connectToChatRoom(int chatId, String userId) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);

        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        // Lazy 로딩을 피하기 위해 명시적으로 connectedUsers를 초기화
        chatRoom.getConnectedUsers().size(); // EAGER 로딩된 상태이므로, size() 호출로 초기화

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

    @Transactional
    public String disconnectFromChatRoom(int chatId, String userId) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);

        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다. id: " + chatId);
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        // Lazy 로딩을 피하기 위해 명시적으로 connectedUsers를 초기화
        chatRoom.getConnectedUsers().size(); // EAGER 로딩된 상태이므로, size() 호출로 초기화

        if (!chatRoom.getConnectedUsers().contains(userId)) {
            return "사용자 " + userId + "는 이 채팅방에 연결되어 있지 않습니다.";
        }

        chatRoom.getConnectedUsers().remove(userId);

        if (chatRoom.getConnectedUsers().isEmpty() && chatRoom.getStatus() != ChatRoomStatus.WAITING) {
            chatRoom.setStatus(ChatRoomStatus.WAITING);
        }

        chatRoomRepository.save(chatRoom);

        return "사용자 " + userId + "가 채팅방 " + chatId + "에서 연결이 종료되었습니다.";
    }

}
