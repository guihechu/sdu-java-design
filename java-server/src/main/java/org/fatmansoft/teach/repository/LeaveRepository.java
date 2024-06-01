package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeaveRepository extends JpaRepository<Leave,Integer> {
    @Query(value = "select max(leaveId) from Leave ")
    Integer getMaxId();
    @Query(value = "from Leave l where ?1='' or l.student.person.num like %?1% or l.student.person.name like %?1% ")
    List<Leave> findLeaveListByNumName(String numName);
    @Query(value = "from Leave  where ?1='' or leaveReason like %?1% ")
    Optional<Leave> findByLeaveReason(String leaveReason);
    @Query(value = "from Leave l where ?1='' or l.student.person.num like %?1% ")
    List<Leave> findByStudentStudentNum(String num);
    @Query(value = "from Leave where student.person.num=?1 and leaveReason=?2 and startDate=?3 and endDate=?4")
    Optional<Leave> findByAll (String num,String leaveReason,String startDate,String endDate);

}