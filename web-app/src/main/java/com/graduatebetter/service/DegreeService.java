package com.graduatebetter.service;

import java.util.List;
import com.graduatebetter.model.DegreeEntity;

public interface DegreeService{
    List<DegreeEntity> getDegree();
    DegreeEntity createDegree(DegreeEntity _degree);
    DegreeEntity selectDegree(Long id);
    DegreeEntity selectDegreeByName(String name);
    boolean degreeExistByName(String name);
    boolean deleteDegree(Long id);
    DegreeEntity updateDegree(DegreeEntity degreeEntity);
}