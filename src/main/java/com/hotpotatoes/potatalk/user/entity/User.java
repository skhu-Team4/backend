package com.hotpotatoes.potatalk.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @Column(name = "loginId", nullable = false, unique = true)
    private String loginId;

    @Column(name = "introduction", nullable = false)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE", nullable = false)
    private Role role;

    @Column(name = "profile_image_url")
    private String profileImageUrl = "/images/default-profile.png";

    public Long getId() {
        return userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Builder
    public User(Long userId, String name, String email, String password, String phoneNumber, String loginId, String introduction, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.loginId = loginId;
        this.introduction = introduction;
        this.profileImageUrl = profileImageUrl;
        this.role = Role.ROLE_USER;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

}
