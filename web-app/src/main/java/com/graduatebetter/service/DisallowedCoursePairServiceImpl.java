package com.graduatebetter.service;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.model.DisallowedCoursePairEntity;
import com.graduatebetter.repository.DisallowedCoursePairRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class DisallowedCoursePairServiceImpl implements DisallowedCoursePairService{
    @Autowired
    private DisallowedCoursePairRepository disallowedCoursePairRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DisallowedCoursePairEntity> getDisallowedCoursePair() {
        return disallowedCoursePairRepository.findAll();
    }

    @Override
    public DisallowedCoursePairEntity selectDisallowedCoursePair(Long _id) {
        if(_id == null) throw new NullPointerException("ID provided is null.");
        if(disallowedCoursePairRepository.findById(_id).isPresent()){
            return disallowedCoursePairRepository.findById(_id).get();
        }
        return null;
    }

    @Override
    public Set<DisallowedCoursePairEntity> selectDisallowedCoursePairByCourse(CourseEntity courseEntity) {
        if(courseEntity == null) throw new NullPointerException("Course entity provided is null.");
        if(!disallowedCoursePairRepository.findByCourseEntity(courseEntity).isEmpty()){
            return disallowedCoursePairRepository.findByCourseEntity(courseEntity);
        }
        return null;
    }

    @Override
    public boolean disallowedCoursePairExist(Long _id) {
        if(_id == null) throw new NullPointerException("ID provided is null.");
        return disallowedCoursePairRepository.findById(_id).isPresent();
    }

    @Override
    public DisallowedCoursePairEntity createDisallowedCoursePair(DisallowedCoursePairEntity _disallowedCoursePairEntity) {
        if(_disallowedCoursePairEntity == null) throw new NullPointerException("Given disallowed course pair entity is null.");
        return disallowedCoursePairRepository.save(_disallowedCoursePairEntity);
    }

    @Override
    public boolean deleteDisallowedCoursePair(Long id) {
        if(id == null) throw new NullPointerException("ID provided is null.");
        if(disallowedCoursePairRepository.findById(id).isPresent()){
            disallowedCoursePairRepository.deleteById(id);
            return true;
        }else{
            System.err.println("Course with id :"+id+" doesn't exist!");
            return false;
        }
    }

    @Transactional
    public DisallowedCoursePairEntity updateDisallowedCoursePair(DisallowedCoursePairEntity disallowedCoursePairEntity) {
        return entityManager.merge(disallowedCoursePairEntity);
    }
}