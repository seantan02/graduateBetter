package com.graduatebetter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.graduatebetter.model.ShortestPathEntity;

public interface ShortestPathResultRepository extends JpaRepository<ShortestPathEntity, Long> {
    // Additional custom queries can be defined here
}