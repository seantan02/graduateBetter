package com.graduatebetter.service;

import java.util.List;
import com.graduatebetter.model.DegreeReqEntity;

public interface DegreeReqService{
    List<DegreeReqEntity> getDegreeReq();
    DegreeReqEntity createDegreeReq(DegreeReqEntity _degreeReq);
    List<DegreeReqEntity> selectDegreeReqByName(String name);
    boolean degreeReqNameExist(String name);
    boolean deleteDegreeReq(Long id);
}