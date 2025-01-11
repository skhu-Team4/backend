package com.hotpotatoes.potatalk.user.dto;

import lombok.Getter;

@Getter
public class UserSignUpDto {
    private String name;
    private String loginId;
    private String email;
    private String password;
    private String phoneNumber;
    private String introduction;
}
