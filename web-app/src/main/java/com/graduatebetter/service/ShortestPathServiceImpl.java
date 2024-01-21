package com.graduatebetter.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.graduatebetter.model.ShortestPathEntity;
import com.graduatebetter.repository.ShortestPathRepository;

@Service
public class ShortestPathServiceImpl implements ShortestPathService{
    @Autowired
    private ShortestPathRepository shortestPathRepository;

    @Override
    public List<ShortestPathEntity> getShortestPath() {
        return shortestPathRepository.findAll();
    }

    @Override
    public ShortestPathEntity createShortestPath(ShortestPathEntity _degree){
        return shortestPathRepository.save(_degree);
    }

	@Override
	public boolean deleteShortestPath(Long id) {
        if(shortestPathRepository.findById(id).isPresent()){
            shortestPathRepository.deleteById(id);
            return true;
        }else{
            System.err.println("Shortest Path with id :"+id+" doesn't exist!");
            return false;
        }
	}
}