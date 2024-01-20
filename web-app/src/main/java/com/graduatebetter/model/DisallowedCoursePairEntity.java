package com.graduatebetter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "disallowed_course_pair")
public class DisallowedCoursePairEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private CourseEntity courseEntity;

    @ManyToOne
    @JoinColumn(name = "disallowed_course_id", referencedColumnName = "id", nullable = false)
    private CourseEntity disallowedCourseEntity;

    @Override
    public boolean equals(Object _disallowedCoursePairEntity) {
        DisallowedCoursePairEntity disallowedCoursePairEntity = (DisallowedCoursePairEntity) _disallowedCoursePairEntity;
        if((this.id != null && this.id == disallowedCoursePairEntity.getId())) return true;
        if(this.courseEntity != null && this.courseEntity.getId() != null && this.courseEntity.getId().equals(disallowedCoursePairEntity.getCourseEntity().getId())
        && this.disallowedCourseEntity != null && this.disallowedCourseEntity.getId() != null && this.disallowedCourseEntity.getId().equals(disallowedCoursePairEntity.getDisallowedCourseEntity().getId())) return true;
        return false;
    }
}
