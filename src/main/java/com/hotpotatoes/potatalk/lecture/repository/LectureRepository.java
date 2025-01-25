package com.hotpotatoes.potatalk.lecture.repository;

import com.hotpotatoes.potatalk.lecture.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    int countByLectureId(Long lectureId);
    List<Lecture> findByLectureNameContaining(String lectureName);
    List<Lecture> findByProfessorContaining(String professor);
}
