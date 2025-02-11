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
    private String email;
    private String loginId;
    private String introduction;
    private String currentImageId;
    private String role;

    // User 엔티티를 매개변수로 받는 추가 생성자
    public UserInfoDto(User user) {
        this.userId = user.getUserId();           // User 엔티티의 userId
        this.email = user.getEmail();             // User 엔티티의 email
        this.loginId = user.getLoginId();         // User 엔티티의 loginId
        this.introduction = user.getIntroduction(); // User 엔티티의 introduction
        this.currentImageId = user.getCurrentImageId(); // User 엔티티의 현재 프로필
        this.role = user.getRole().name();        // User 엔티티의 Role을 문자열로 변환
    }

    public static UserInfoDto from(User user) {
        return UserInfoDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .loginId(user.getLoginId())
                .introduction(user.getIntroduction())
                .currentImageId(user.getProfileImageUrl())
                .build();
    }

}

