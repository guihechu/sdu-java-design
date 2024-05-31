package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.PreCollege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PreCollegeRepository extends JpaRepository<PreCollege,Integer> {
    @Query(value = "select max(infoId) from PreCollege")
    Integer getMaxId();

    Optional<PreCollege> findByStudentStudentId(Integer studentId);

    @Query(value = "from PreCollege")
    List<PreCollege> findAll();
}
