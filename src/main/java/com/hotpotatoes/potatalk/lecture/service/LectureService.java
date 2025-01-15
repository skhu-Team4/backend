package com.hotpotatoes.potatalk.lecture.service;

import com.hotpotatoes.potatalk.lecture.domain.Lecture;
import com.hotpotatoes.potatalk.lecture.dto.LectureReqDto;
import com.hotpotatoes.potatalk.lecture.dto.LectureResDto;
import com.hotpotatoes.potatalk.lecture.dto.PageResDto;
import com.hotpotatoes.potatalk.lecture.exception.LectureException;
import com.hotpotatoes.potatalk.lecture.exception.LectureErrorCode;
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
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    // 전체 조회 - 페이지네이션 적용
    public PageResDto<LectureResDto> getAllLectures(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PageResDto.of(lectureRepository.findAll(pageable)
                .map(LectureResDto::of));
    }

    // 수업 참여 인원 조회
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

    // 내 강의 추가
    @Transactional
    public LectureResDto addMyLecture(Long lectureId, String username) {
        // 사용자와 강의 조회
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException(LectureErrorCode.LECTURE_NOT_FOUND));

        // 이미 추가된 강의인지 확인
        if (user.getLectures().contains(lecture)) {
            throw new LectureException(LectureErrorCode.LECTURE_ALREADY_EXISTS);
        }

        // 사용자의 강의 목록에 추가
        user.getLectures().add(lecture);
        userRepository.save(user);

        return LectureResDto.of(lecture);
    }

    // 내 강의 목록 조회
    public List<LectureResDto> getMyLectures(String username) {
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return user.getLectures().stream()
                .map(LectureResDto::of)
                .collect(Collectors.toList());
    }

    // 수업 삭제
    @Transactional
    public void deleteLecture(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException(LectureErrorCode.LECTURE_NOT_FOUND));
        lectureRepository.delete(lecture);
    }

    // 내 강의 삭제
    @Transactional
    public void deleteMyLecture(Long lectureId, String username) {
        // 사용자와 강의 조회
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException(LectureErrorCode.LECTURE_NOT_FOUND));

        // 사용자의 강의 목록에서 해당 강의가 있는지 확인
        if (!user.getLectures().contains(lecture)) {
            throw new LectureException(LectureErrorCode.LECTURE_NOT_FOUND);
        }

        // 사용자의 강의 목록에서 제거
        user.getLectures().remove(lecture);
        userRepository.save(user);
    }
}
