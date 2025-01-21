package com.hotpotatoes.potatalk.lecture.repository;

import com.hotpotatoes.potatalk.lecture.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    int countByLectureId(Long lectureId);
    List<Lecture> findByLectureNameContaining(String lectureName);
    List<Lecture> findByProfessorContaining(String professor);
}
