package com.hotpotatoes.potatalk.chat.dto.chat.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchAcceptRequestDto {
    private String userId;
    private String matchedUser;
}
