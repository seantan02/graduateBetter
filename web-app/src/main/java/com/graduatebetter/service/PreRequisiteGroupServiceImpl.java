package com.graduatebetter.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.model.PreRequisiteGroupEntity;
import com.graduatebetter.repository.PreRequisiteGroupRepository;

@Service
public class PreRequisiteGroupServiceImpl implements PreRequisiteGroupService{
    @Autowired
    private PreRequisiteGroupRepository preRequisiteGroupRepository;

    @Override
    public List<PreRequisiteGroupEntity> getPreRequisiteGroup() {
        return preRequisiteGroupRepository.findAll();
    }

    @Override
    public boolean preRequisiteGroupExist(CourseEntity preRequisite) {
        return preRequisiteGroupRepository.findByCourseEntity(preRequisite).isEmpty();
    }

    @Override
    public PreRequisiteGroupEntity selectPreRequisiteGroup(Long _id) {
        if(preRequisiteGroupRepository.findById(_id).isPresent()) return preRequisiteGroupRepository.findById(_id).get();
        return null;
    }

    @Override
    public PreRequisiteGroupEntity createPreRequisiteGroup(PreRequisiteGroupEntity _preRequisiteGroupEntity) {
        return preRequisiteGroupRepository.save(_preRequisiteGroupEntity);
    }

    @Override
    public boolean deletePreRequisiteGroup(Long id) {
        if(preRequisiteGroupRepository.findById(id).isPresent()){
            preRequisiteGroupRepository.deleteById(id);
            return true;
        }
        return false;
    }

    
}