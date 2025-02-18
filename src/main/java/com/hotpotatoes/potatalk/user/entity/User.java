package com.hotpotatoes.potatalk.user.entity;

import com.hotpotatoes.potatalk.lecture.domain.Lecture;
import com.hotpotatoes.potatalk.user.entity.Role;
import com.hotpotatoes.potatalk.user.type.ProfileImageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "loginId", nullable = false, unique = true)
    private String loginId;

    @Column(name = "introduction", nullable = false)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE", nullable = false)
    private Role role;

    @Column(name = "profile_image_url")
    private String profileImageUrl = "/images/default-profile.png";

    @Column(name = "current_image_id", nullable = false)
    private String currentImageId = ProfileImageType.PROFILE_1.getImageId();

    @Column(name = "refresh_token")
    private String refreshToken;

    @ManyToMany(mappedBy = "users", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Lecture> lectures = new HashSet<>();

    @Builder
    public User(Long userId, String email, String password,
                String loginId, String introduction, String profileImageUrl) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.loginId = loginId;
        this.introduction = introduction;
        this.profileImageUrl = profileImageUrl;
        this.currentImageId = ProfileImageType.PROFILE_1.getImageId(); // 기본값 설정
        this.role = Role.ROLE_USER;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setCurrentImageId(String currentImageId) {
        this.currentImageId = currentImageId;
    }
    // 강의 추가
    public void addLecture(Lecture lecture) {
        this.lectures.add(lecture);
        lecture.getUsers().add(this);
    }

    // 강의 제거
    public void removeLecture(Lecture lecture) {
        this.lectures.remove(lecture);
        lecture.getUsers().remove(this);
    }

}

