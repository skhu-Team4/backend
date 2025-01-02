package com.hotpotatoes.potatalk.chat.repository;

import com.hotpotatoes.potatalk.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
}
