package com.graduatebetter.service;

import java.util.List;
import com.graduatebetter.model.ShortestPathEntity;

public interface ShortestPathService{
    List<ShortestPathEntity> getShortestPath();
    ShortestPathEntity createShortestPath(ShortestPathEntity _degree);
    boolean deleteShortestPath(Long id);
}