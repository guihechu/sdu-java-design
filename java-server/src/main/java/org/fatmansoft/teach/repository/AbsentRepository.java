package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Absent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AbsentRepository extends JpaRepository<Absent,Integer>{
    @Query(value = "select max(absentId) from Absent")
    Integer getMaxId();

    List<Absent> findByStudentStudentId(Integer studentId);
    @Query(value="from Absent where (?1=0 or student.studentId=?1) and (?2=0 or course.courseId=?2)" )
    List<Absent> findByStudentCourse(Integer studentId, Integer courseId);
}
