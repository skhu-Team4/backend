package com.hotpotatoes.potatalk.chat.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ChatPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int photo_id;

    @Column(length = 255, nullable = false, updatable = false)
    private String photo_url;
}