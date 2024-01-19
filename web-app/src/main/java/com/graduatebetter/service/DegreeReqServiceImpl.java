package com.graduatebetter.service;

import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.graduatebetter.model.DegreeReqEntity;
import com.graduatebetter.repository.DegreeReqRepository;

@Service
public class DegreeReqServiceImpl implements DegreeReqService{
    @Autowired
    private DegreeReqRepository degreeReqRepository;

    @Override
    public List<DegreeReqEntity> getDegreeReq() {
        return degreeReqRepository.findAll();
    }

    @Override
    public DegreeReqEntity createDegreeReq(DegreeReqEntity _degreeReq){
        return degreeReqRepository.save(_degreeReq);
    }

    @Override
    public List<DegreeReqEntity> selectDegreeReqByName(String name) {
        if(degreeReqRepository.findByName(name).isEmpty()){
            return degreeReqRepository.findByName(name);
        }
        return new ArrayList<DegreeReqEntity>();
    }

    @Override
    public boolean degreeReqNameExist(String name) {
        return degreeReqRepository.findByName(name).isEmpty();
    }
	@Override
	public boolean deleteDegreeReq(Long id) {
        if(degreeReqRepository.findById(id).isPresent()){
            degreeReqRepository.deleteById(id);
            return true;
        }else{
            System.err.println("Degree Requirement with id :"+id+" doesn't exist!");
            return false;
        }
	}
}