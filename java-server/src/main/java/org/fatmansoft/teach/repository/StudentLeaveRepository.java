package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.StudentLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentLeaveRepository extends JpaRepository<StudentLeave,Integer> {
    @Query(value = "select max(studentLeaveId) from StudentLeave")
    Integer getMaxId();
    @Query(value = "from StudentLeave sl where ?1='' or sl.student.person.num like %?1% ")
    List<StudentLeave> findByStudentNum(String num);
    @Query(value = "from StudentLeave sl where ?1='' or sl.student.person.num like %?1% ")
    Optional<StudentLeave> findByStudentStudentNum(String num);
    @Query(value = "from StudentLeave sl where ?1='' or sl.student.person.num like %?1% or sl.student.person.name like %?1% ")
    List<StudentLeave> findStudentLeaveListByNumName(String numName);
    @Query(value = "from StudentLeave sl  where ?1='' or sl.leave.leaveReason like %?1% ")
    Optional<StudentLeave> findStudentLeaveByLeaveReason(String leaveReason);



}
