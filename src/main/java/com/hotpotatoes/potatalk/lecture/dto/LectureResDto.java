package com.hotpotatoes.potatalk.lecture.dto;

import com.hotpotatoes.potatalk.lecture.domain.Lecture;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PROTECTED)
public class LectureResDto {
    private String lectureName;
    private String major;
    private String professor;

    public static LectureResDto of(Lecture lecture) {
        return LectureResDto.builder()
                .lectureName(lecture.getLectureName())
                .major(lecture.getMajor())
                .professor(lecture.getProfessor())
                .build();
    }

}