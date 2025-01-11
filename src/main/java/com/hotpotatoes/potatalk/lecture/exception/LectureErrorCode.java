package com.hotpotatoes.potatalk.lecture.exception;

import lombok.Getter;

@Getter
public enum LectureErrorCode {
    LECTURE_NOT_FOUND("강의를 찾을 수 없습니다."),
    INVALID_LECTURE_REQUEST("잘못된 강의 정보입니다.");

    private final String message;

    LectureErrorCode(String message) {
        this.message = message;
    }
}
