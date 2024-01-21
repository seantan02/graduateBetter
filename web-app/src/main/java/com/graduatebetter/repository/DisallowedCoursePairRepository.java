package com.graduatebetter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.model.DisallowedCoursePairEntity;
import java.util.Set;


public interface DisallowedCoursePairRepository extends JpaRepository<DisallowedCoursePairEntity, Long> {
    // Additional custom queries can be defined here
    Set<DisallowedCoursePairEntity> findByCourseEntity(CourseEntity courseEntity);
    Set<DisallowedCoursePairEntity> findByDisallowedCourseEntity(CourseEntity disallowedCourseEntity);
}