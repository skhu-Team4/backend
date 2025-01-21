package com.hotpotatoes.potatalk.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoDto {
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String loginId;
    private String introduction;
    private String profileImageUrl;
    private String role;
}
