package com.hotpotatoes.potatalk.lecture.controller;

import com.hotpotatoes.potatalk.lecture.domain.Lecture;
import com.hotpotatoes.potatalk.lecture.dto.LectureReqDto;
import com.hotpotatoes.potatalk.lecture.dto.LectureResDto;
import com.hotpotatoes.potatalk.lecture.dto.PageResDto;
import com.hotpotatoes.potatalk.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/lecture")
@RequiredArgsConstructor
@RestController
public class LectureController {
    private final LectureService lectureService;

    // 전체조회 - 페이지네이션
    @GetMapping
    public ResponseEntity<PageResDto<LectureResDto>> getAllLectures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(lectureService.getAllLectures(page, size));
    }

    // 수업 채팅방 참여 인원 조회
    @GetMapping("/{lecture-id}/participants")
    public ResponseEntity<Integer> getLectureParticipant(@PathVariable("lecture-id") Long lectureId) {
        return ResponseEntity.ok(lectureService.getParticipantCountByLectureId(lectureId));
    }

    // 강의명으로 조회
    @GetMapping("/lectureName/{lectureName}")
    public ResponseEntity<List<LectureResDto>> getLectureByLectureName(@PathVariable String lectureName) {
        return ResponseEntity.ok((lectureService.getLectureByLectureName(lectureName)));
    }

    // 교수명으로 조회
    @GetMapping("/professor/{professor}")
    public ResponseEntity<List<LectureResDto>> getLectureByProfessor(@PathVariable String professor) {
        return ResponseEntity.ok((lectureService.getLectureByProfessor(professor)));
    }

    // 수업 추가
    @PostMapping
    public ResponseEntity<LectureResDto> addLecture(@RequestBody LectureReqDto lectureReqDto) {
        return ResponseEntity.ok(lectureService.addLecture(lectureReqDto));
    } 

    // 내가 선택한 강의 추가
    @PostMapping("/my/{lecture-id}")
    public ResponseEntity<LectureResDto> addMyLecture(
            @PathVariable("lecture-id") Long lectureId,
            Principal principal) {
        String username = principal.getName();
        System.out.println("Principal name: " + username);  // 로그 추가
        return ResponseEntity.ok(lectureService.addMyLecture(lectureId, username));
    }
    // 내가 선택한 강의 목록 조회
    @GetMapping("/my")
    public ResponseEntity<List<LectureResDto>> getMyLectures(Principal principal) {
        return ResponseEntity.ok(lectureService.getMyLectures(principal.getName()));
    }

    // 수업 삭제
    @DeleteMapping("/{lecture-id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable("lecture-id") Long lectureId) {
        lectureService.deleteLecture(lectureId);
        return ResponseEntity.ok().build();
    }

    // 내가 선택한 강의 삭제
    @DeleteMapping("/my/{lecture-id}")
    public ResponseEntity<Void> deleteMyLecture(
            @PathVariable("lecture-id") Long lectureId,
            Principal principal) {
        lectureService.deleteMyLecture(lectureId, principal.getName());
        return ResponseEntity.ok().build();
    }
}