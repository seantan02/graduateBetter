package com.graduatebetter.service;

import java.util.List;
import java.util.Set;

import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.model.DisallowedCoursePairEntity;

public interface DisallowedCoursePairService {
    //accessors
    List<DisallowedCoursePairEntity> getDisallowedCoursePair();
    DisallowedCoursePairEntity selectDisallowedCoursePair(Long _id);
    Set<DisallowedCoursePairEntity> selectDisallowedCoursePairByCourse(CourseEntity courseEntity);
    boolean disallowedCoursePairExist(Long id);
    //mutator
    DisallowedCoursePairEntity createDisallowedCoursePair(DisallowedCoursePairEntity _disallowedCoursePairEntity);
    boolean deleteDisallowedCoursePair(Long id);
    DisallowedCoursePairEntity updateDisallowedCoursePair(DisallowedCoursePairEntity _disallowedCoursePairEntity);
}
