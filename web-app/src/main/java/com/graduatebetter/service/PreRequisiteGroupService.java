package com.graduatebetter.service;

import java.util.List;
import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.model.PreRequisiteGroupEntity;

public interface PreRequisiteGroupService{
    List<PreRequisiteGroupEntity> getPreRequisiteGroup();
    boolean preRequisiteGroupExist(CourseEntity preRequisite);
    PreRequisiteGroupEntity selectPreRequisiteGroup(Long _id);
    PreRequisiteGroupEntity createPreRequisiteGroup(PreRequisiteGroupEntity _preRequisiteGroupEntity);
    boolean deletePreRequisiteGroup(Long id);
}