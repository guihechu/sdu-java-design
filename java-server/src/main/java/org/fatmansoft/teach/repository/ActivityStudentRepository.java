package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.ActivityStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityStudentRepository extends JpaRepository<ActivityStudent,Integer> {
    @Query(value = "select max(activityStudentId) from ActivityStudent  ")
    Integer getMaxId();
    @Query(value="from ActivityStudent where (?1=0 or student.studentId=?1) and (?2=0 or activity.activityId=?2)" )
    List<ActivityStudent> findByStudentActivity(Integer studentId, Integer activityId);


    @Query(value = "from ActivityStudent where activity.name = ?1 or signUp = ?2")
    List<ActivityStudent> findByNameAndSignUp(String name, String signUp);


}