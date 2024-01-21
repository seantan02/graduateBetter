package com.graduatebetter.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.model.PreRequisiteEntity;
import com.graduatebetter.repository.PreRequisiteRepository;

@Service
public class PreRequisiteServiceImpl implements PreRequisiteService{
    @Autowired
    private PreRequisiteRepository preRequisiteRepository;

    @Override
    public List<PreRequisiteEntity> getPreRequisite() {
        return preRequisiteRepository.findAll();
    }

    @Override
    public boolean preRequisiteExist(CourseEntity preRequisite) {
        return preRequisiteRepository.findByCoursePreRequisiteEntity(preRequisite).isEmpty();
    }

    @Override
    public PreRequisiteEntity selectPreRequisite(Long _id) {
        if(preRequisiteRepository.findById(_id).isPresent()){
            return preRequisiteRepository.findById(_id).get();
        }
        return null;
    }

    @Override
    public List<PreRequisiteEntity> selectPreRequisiteByCourse(CourseEntity _course) {
        return preRequisiteRepository.findByCoursePreRequisiteEntity(_course);
    }

    @Override
    public PreRequisiteEntity createPreRequisite(PreRequisiteEntity _preRequisite){
        return preRequisiteRepository.save(_preRequisite);
    }

	@Override
	public boolean deletePreRequisite(Long id) {
        if(preRequisiteRepository.findById(id).isPresent()){
            preRequisiteRepository.deleteById(id);
            return true;
        }else{
            System.err.println("Degree with id :"+id+" doesn't exist!");
            return false;
        }
	}
}