package com.graduatebetter.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.repository.CourseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class CourseServiceImpl implements CourseService{
    @Autowired
    private CourseRepository courseRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CourseEntity> getCourse() {
        return courseRepository.findAll();
    }

    @Override
    public CourseEntity selectCourse(Long _id) {
        if(courseRepository.findById(_id).isPresent()){
            return courseRepository.findById(_id).get();
        }
        return null;
    }

    @Override
    public CourseEntity selectCourseByCode(String code) {
        if(courseRepository.findByCode(code).isPresent()){
            return courseRepository.findByCode(code).get();
        }
        return null;
    }

    @Override
    public boolean courseExistByCode(String code) {
        return courseRepository.findByCode(code).isPresent();
    }

    /* Mutator starts here!!!!!!! */
    
    @Override
    public CourseEntity createCourse(CourseEntity _course){
        return courseRepository.save(_course);
    }

	@Override
	public boolean deleteCourse(Long id) {
        if(courseRepository.findById(id).isPresent()){
            courseRepository.deleteById(id);
            return true;
        }else{
            System.err.println("Course with id :"+id+" doesn't exist!");
            return false;
        }
	}

    @Transactional
    public CourseEntity updateCourse(CourseEntity courseEntity) {
        return entityManager.merge(courseEntity);
    }
}