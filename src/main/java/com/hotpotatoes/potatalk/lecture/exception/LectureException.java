package com.hotpotatoes.potatalk.lecture.exception;

import lombok.Getter;

@Getter
public class LectureException extends RuntimeException {
    private final LectureErrorCode errorCode;

    public LectureException(LectureErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
