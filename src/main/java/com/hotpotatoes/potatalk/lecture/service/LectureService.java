package com.hotpotatoes.potatalk.lecture.service;

import com.hotpotatoes.potatalk.lecture.domain.Lecture;
import com.hotpotatoes.potatalk.lecture.dto.LectureReqDto;
import com.hotpotatoes.potatalk.lecture.dto.LectureResDto;
import com.hotpotatoes.potatalk.lecture.dto.PageResDto;
import com.hotpotatoes.potatalk.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureService {
    final LectureRepository lectureRepository;

    // 전체 조회 - 페이지네이션 적용
    public PageResDto<LectureResDto> getAllLectures(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResDto.of(lectureRepository.findAll(pageable)
                .map(LectureResDto::of));
    }

    // 수업 추가 인원 조회
    public int getParticipantCountByLectureId(Long lectureId) {
        return lectureRepository.countByLectureId(lectureId);
    }

    // 강의명으로 조회 (부분 일치)
    public List<LectureResDto> getLectureByLectureName(String lectureName) {
        return lectureRepository.findByLectureNameContaining(lectureName).stream()
                .map(LectureResDto::of)
                .collect(Collectors.toList());
    }

    // 교수명으로 조회 (부분 일치)
    public List<LectureResDto> getLectureByProfessor(String professor) {
        return lectureRepository.findByProfessorContaining(professor).stream()
                .map(LectureResDto::of)
                .collect(Collectors.toList());
    }

    // 수업 추가
    @Transactional
    public LectureResDto addLecture(LectureReqDto lectureReqDto) {
        Lecture lecture = lectureReqDto.toEntity();
        Lecture savedLecture = lectureRepository.save(lecture);
        return LectureResDto.of(savedLecture);
    }

    // 수업 삭제
    public void deleteLecture(Long lectureId) {
        lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의가 존재하지 않습니다."));
    }
}
