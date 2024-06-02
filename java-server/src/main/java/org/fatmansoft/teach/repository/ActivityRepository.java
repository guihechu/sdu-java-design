package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    @Query(value = "select max(activityId) from Activity  ")
    Integer getMaxId();
    Optional<Activity> findByNum(String num);
    Optional<Activity> findByActivityId (Integer activityId);
    @Query(value = "from Activity where ?1='' or num like %?1% or name like %?1% ")
    List<Activity> findActivityListByNumName(String numName);

}
