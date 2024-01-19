package com.graduatebetter.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.graduatebetter.model.CourseEntity;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    // Additional custom queries can be defined here
    Optional<CourseEntity> findByCode(String code);
}