package com.graduatebetter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.graduatebetter.model.DegreeReqEntity;
import java.util.List;


public interface DegreeReqRepository extends JpaRepository<DegreeReqEntity, Long> {
    // Additional custom queries can be defined here
    List<DegreeReqEntity> findByName(String name);
}