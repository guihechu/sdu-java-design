package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.RejectLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RejectLeaveRepository extends JpaRepository<RejectLeave,Integer>{
    @Query(value = "select max(rejectLeaveId) from RejectLeave")
    Integer getMaxId();
    @Query(value = "from RejectLeave sl where ?1='' or sl.studentLeave.student.person.num like %?1% or sl.studentLeave.student.person.name like %?1% ")
    List<RejectLeave> findRejectLeaveListByNumName(String numName);
    @Query(value = "from RejectLeave sl where ?1='' or sl.studentLeave.student.person.num like %?1% ")
    List<RejectLeave> findByStudentNum(String num);
}