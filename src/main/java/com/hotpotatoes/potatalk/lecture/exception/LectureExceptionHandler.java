package com.hotpotatoes.potatalk.lecture.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LectureExceptionHandler {

    @ExceptionHandler(LectureException.class)
    public ResponseEntity<ErrorResponse> handleLectureException(LectureException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @Getter
    @AllArgsConstructor
    static class ErrorResponse {
        private String message;
    }
}
