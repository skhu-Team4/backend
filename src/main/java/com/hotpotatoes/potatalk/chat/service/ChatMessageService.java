package com.hotpotatoes.potatalk.chat.service;

import com.hotpotatoes.potatalk.chat.domain.ChatMessage;
import com.hotpotatoes.potatalk.chat.domain.ChatRoom;
import com.hotpotatoes.potatalk.chat.domain.ChatRoomStatus;
import com.hotpotatoes.potatalk.chat.dto.ChatMessageDto;
import com.hotpotatoes.potatalk.chat.repository.ChatMessageRepository;
import com.hotpotatoes.potatalk.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void saveMessage(int chatId, ChatMessageDto messageDto) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);

        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageContent(messageDto.getContent());
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessage.setChatRoom(chatRoom);

        chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public void deleteMessage(int messageId) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다. id : " + messageId));
        chatMessageRepository.delete(chatMessage);
    }

    @Transactional
    public void readMessage(int chatId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다. id : " + chatId));

        List<ChatMessage> chatMessageList = chatRoom.getMessages();

        if(chatRoom.getStatus() == ChatRoomStatus.IN_CHAT) {
            chatMessageList.forEach(chatMessage -> {
                if (!chatMessage.getIsRead()) {
                    chatMessage.setIsRead(true);
                }
                chatMessageRepository.save(chatMessage);
            });
        }
    }

    @Transactional
    public long getUnreadMessageCount(int chatId) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatId);
        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();

        return chatMessageRepository.countByChatRoomAndIsReadFalse(chatRoom);
    }
}
