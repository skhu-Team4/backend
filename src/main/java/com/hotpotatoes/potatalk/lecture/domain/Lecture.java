package com.hotpotatoes.potatalk.lecture.domain;

import com.hotpotatoes.potatalk.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_lecture",
            joinColumns = @JoinColumn(name = "lecture_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id")
    private Lecture toId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_id")
    private Lecture fromId;

    @Builder
    public Lecture(String lectureName, String major, String professor,
                   Lecture toId, Lecture fromId) {
        this.lectureName = lectureName;
        this.major = major;
        this.professor = professor;
        this.toId = toId;
        this.fromId = fromId;
    }

    // 연관관계 편의 메서드
    public void addUser(User user) {
        this.users.add(user);
        user.getLectures().add(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.getLectures().remove(this);
    }
}