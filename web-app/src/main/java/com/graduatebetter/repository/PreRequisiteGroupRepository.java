package com.graduatebetter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.model.PreRequisiteGroupEntity;
import java.util.List;




public interface PreRequisiteGroupRepository extends JpaRepository<PreRequisiteGroupEntity, Long> {
    List<PreRequisiteGroupEntity> findByCourseEntity(CourseEntity courseEntity);
}