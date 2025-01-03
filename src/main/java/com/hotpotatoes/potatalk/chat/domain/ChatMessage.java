package com.hotpotatoes.potatalk.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int message_id;

    @Column(nullable = false, updatable = false)
    private String messageContent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatRoom chatRoom;
}
