package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Course 数据操作接口，主要实现Course数据的查询操作
 */

@Repository
public interface CourseRepository extends JpaRepository<Course,Integer> {
    @Query(value = "select max(courseId) from Course  ")
    Integer getMaxId();
    Optional<Course> findByCourseId(Integer courseId);
    Optional<Course> findByNum(String num);

//    @Query("SELECT c FROM Course c WHERE (:numName IS NULL OR c.num LIKE CONCAT('%', :numName, '%') OR c.name LIKE CONCAT('%', :numName, '%'))")
//    List<Course> findCourseListByNumName(String numName);
    @Query("from Course where :numName = '' or numName like concat('%', :numName, '%')")
    List<Course> findCourseListByNumName(@Param("numName") String numName);
}
