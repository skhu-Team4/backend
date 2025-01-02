package com.hotpotatoes.potatalk.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResponseDto {
    private boolean accepted; // true: 수락, false: 거절
}
