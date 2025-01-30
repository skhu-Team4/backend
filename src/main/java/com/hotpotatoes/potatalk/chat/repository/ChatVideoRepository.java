package com.hotpotatoes.potatalk.chat.repository;

import com.hotpotatoes.potatalk.chat.domain.ChatVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatVideoRepository extends JpaRepository<ChatVideo, Integer> {
}
