package com.graduatebetter.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.graduatebetter.model.DegreeEntity;
import com.graduatebetter.repository.DegreeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class DegreeServiceImpl implements DegreeService{
    @Autowired
    private DegreeRepository degreeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DegreeEntity> getDegree() {
        return degreeRepository.findAll();
    }

    @Override
    public DegreeEntity createDegree(DegreeEntity _degree){
        return degreeRepository.save(_degree);
    }

    @Override
    public DegreeEntity selectDegree(Long id) {
        if(degreeRepository.findById(id).isPresent()){
            return degreeRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public DegreeEntity selectDegreeByName(String name) {
         if(degreeRepository.findByName(name) != null){
            return degreeRepository.findByName(name);
        }
        return null;
    }

    @Override
    public boolean degreeExistByName(String name) {
        return degreeRepository.findByName(name)!=null;
    }

	@Override
	public boolean deleteDegree(Long id) {
        if(degreeRepository.findById(id).isPresent()){
            degreeRepository.deleteById(id);
            return true;
        }else{
            System.err.println("Degree with id :"+id+" doesn't exist!");
            return false;
        }
	}

    @Transactional
    public DegreeEntity updateDegree(DegreeEntity degreeEntity) {
        return entityManager.merge(degreeEntity);
    }
}