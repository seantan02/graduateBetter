package com.graduatebetter.service;

import java.util.List;

import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.model.PreRequisiteEntity;

public interface PreRequisiteService{
    List<PreRequisiteEntity> getPreRequisite();
    boolean preRequisiteExist(CourseEntity preRequisite);
    PreRequisiteEntity selectPreRequisite(Long _id);
    List<PreRequisiteEntity> selectPreRequisiteByCourse(CourseEntity _course);
    PreRequisiteEntity createPreRequisite(PreRequisiteEntity _preRequisiteEntity);
    boolean deletePreRequisite(Long id);
}