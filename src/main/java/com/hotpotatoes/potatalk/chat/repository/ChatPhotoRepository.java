package com.hotpotatoes.potatalk.chat.repository;

import com.hotpotatoes.potatalk.chat.domain.ChatPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatPhotoRepository extends JpaRepository<ChatPhoto, Integer> {
}
