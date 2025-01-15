package com.hotpotatoes.potatalk.lecture.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PROTECTED)
public class Lecture {

    @Id
    @Column(name="lecture_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    @Column(nullable = false)
    private String lectureName;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private String professor;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="to_id")
    private Lecture toId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="from_id")
    private Lecture fromId;

}
