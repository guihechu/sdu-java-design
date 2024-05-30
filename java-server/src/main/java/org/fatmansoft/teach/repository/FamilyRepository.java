package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyRepository extends JpaRepository<Family,Integer> {
    @Query(value = "select max(familyId) from Family")
    Integer getMaxId();

    @Query(value = "from Family where ?1='' or name like %?1% ")
    List<Family> findByName(String name);

    Optional<Family> findByIdCard(String idCard);


}
