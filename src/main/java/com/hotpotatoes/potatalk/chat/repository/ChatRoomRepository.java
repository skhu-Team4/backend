package com.hotpotatoes.potatalk.chat.repository;

import com.hotpotatoes.potatalk.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
}
