package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Integer> {

    @Query(value = "select max(scoreId) from Score  ")
    Integer getMaxId();

    @Query(value = "from Project where ?1='' or name like %?1%")
    List<Project> findByName(String name);

    @Query(value = "from Project where student.person.num=?1")
    List<Project> findByStudentNum(String num);


}
