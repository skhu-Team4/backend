package com.hotpotatoes.potatalk.lecture.dto;

import com.hotpotatoes.potatalk.lecture.domain.Lecture;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@AllArgsConstructor(access= AccessLevel.PROTECTED)
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class LectureReqDto {

    @NotBlank(message = "강의명은 필수입니다")
    private String lectureName;

    @NotBlank(message = "전공은 필수입니다")
    private String major;

    @NotBlank(message = "교수명은 필수입니다")
    private String professor;
    public Lecture toEntity() {
        return Lecture.builder()
                .lectureName(this.lectureName)
                .major(this.major)
                .professor(this.professor)
                .build();
    }
}