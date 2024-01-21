package com.graduatebetter.service;

import java.util.List;
import com.graduatebetter.model.CourseEntity;

public interface CourseService{
    //accessors
    List<CourseEntity> getCourse();
    CourseEntity selectCourse(Long _id);
    CourseEntity selectCourseByCode(String code);
    boolean courseExistByCode(String code);
    //mutator
    CourseEntity createCourse(CourseEntity _course);
    boolean deleteCourse(Long id);
    CourseEntity updateCourse(CourseEntity courseEntity);
}