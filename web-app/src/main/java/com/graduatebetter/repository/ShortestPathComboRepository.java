package com.graduatebetter.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.graduatebetter.model.ShortestPathComboEntity;

public interface ShortestPathComboRepository extends JpaRepository<ShortestPathComboEntity, Long> {
    // Additional custom queries can be defined here
}