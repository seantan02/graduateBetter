package com.graduatebetter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.graduatebetter.model.DegreeEntity;




public interface DegreeRepository extends JpaRepository<DegreeEntity, Long> {
    // Additional custom queries can be defined here
    DegreeEntity findByName(String name);
}