package com.hotpotatoes.potatalk.lecture.domain;
import com.hotpotatoes.potatalk.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter  // Setter 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture {
    @Id
    @Column(name = "lecture_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    @Column(nullable = false)
    private String lectureName;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private String professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // private 필드지만 getter/setter가 필요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id")
    private Lecture toId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id")
    private Lecture fromId;

    @Builder
    public Lecture(String lectureName, String major, String professor, User user, Lecture toId, Lecture fromId) {
        this.lectureName = lectureName;
        this.major = major;
        this.professor = professor;
        this.user = user;
        this.toId = toId;
        this.fromId = fromId;
    }

    // 연관관계 편의 메서드 추가
    public void setUser(User user) {
        this.user = user;
        user.getLectures().add(this);  // 양방향 관계 설정
    }
}
