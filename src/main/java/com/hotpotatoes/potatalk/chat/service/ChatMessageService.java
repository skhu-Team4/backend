package com.hotpotatoes.potatalk.chat.service;

import com.hotpotatoes.potatalk.chat.domain.ChatMessage;
import com.hotpotatoes.potatalk.chat.domain.ChatRoom;
import com.hotpotatoes.potatalk.chat.dto.ChatMessageDto;
import com.hotpotatoes.potatalk.chat.repository.ChatMessageRepository;
import com.hotpotatoes.potatalk.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

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

    public void deleteMessage(int messageId) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다. id : " + messageId));
        chatMessageRepository.delete(chatMessage);
    }
}
