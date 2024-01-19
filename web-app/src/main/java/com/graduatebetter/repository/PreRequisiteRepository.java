package com.graduatebetter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.model.PreRequisiteEntity;
import com.graduatebetter.model.PreRequisiteGroupEntity;

import java.util.List;



public interface PreRequisiteRepository extends JpaRepository<PreRequisiteEntity, Long> {
    List<PreRequisiteEntity> findByCoursePreRequisiteEntity(CourseEntity coursePreRequisiteEntity);
    List<PreRequisiteEntity> findByPreRequisiteGroupEntity(PreRequisiteGroupEntity preRequisiteGroupEntity);
}