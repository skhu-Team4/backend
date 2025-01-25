package com.hotpotatoes.potatalk.user.dto;

import com.hotpotatoes.potatalk.user.entity.User; // User 엔티티 import
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

    // User 엔티티를 매개변수로 받는 추가 생성자
    public UserInfoDto(User user) {
        this.userId = user.getUserId();           // User 엔티티의 userId
        this.name = user.getName();               // User 엔티티의 name
        this.email = user.getEmail();             // User 엔티티의 email
        this.phoneNumber = user.getPhoneNumber(); // User 엔티티의 phoneNumber
        this.loginId = user.getLoginId();         // User 엔티티의 loginId
        this.introduction = user.getIntroduction(); // User 엔티티의 introduction
        this.profileImageUrl = user.getProfileImageUrl(); // User 엔티티의 profileImageUrl
        this.role = user.getRole().name();        // User 엔티티의 Role을 문자열로 변환
    }

    public static UserInfoDto from(User user) {
        return UserInfoDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .loginId(user.getPhoneNumber())
                .introduction(user.getIntroduction())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}