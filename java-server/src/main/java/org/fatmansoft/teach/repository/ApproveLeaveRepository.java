package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.ApproveLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApproveLeaveRepository extends JpaRepository<ApproveLeave,Integer>{
    @Query(value = "select max(approveLeaveId) from ApproveLeave")
    Integer getMaxId();
    @Query(value = "from ApproveLeave sl where ?1='' or sl.studentLeave.student.person.num like %?1% or sl.studentLeave.student.person.name like %?1% ")
    List<ApproveLeave> findApproveLeaveListByNumName(String numName);
    @Query(value = "from ApproveLeave sl where ?1='' or sl.studentLeave.student.person.num like %?1% ")
    List<ApproveLeave> findByStudentNum(String num);
}
