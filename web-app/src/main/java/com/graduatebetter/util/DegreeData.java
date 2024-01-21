package com.graduatebetter.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.model.DegreeEntity;
import com.graduatebetter.model.DegreeReqEntity;
import com.graduatebetter.model.DisallowedCoursePairEntity;
import com.graduatebetter.model.PreRequisiteEntity;
import com.graduatebetter.model.PreRequisiteGroupEntity;
import com.graduatebetter.service.CourseService;
import com.graduatebetter.service.DegreeReqService;
import com.graduatebetter.service.DegreeService;
import com.graduatebetter.service.DisallowedCoursePairService;
import com.graduatebetter.service.PreRequisiteService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class DegreeData {
    private int numberOfCourses;
    private HashSet<String> degree;
    private HashMap<String, List<Course>> degreeToCourse;
    private HashMap<String, HashSet<String>> degreeRequirements;
    private HashMap<String, Integer> requirementToCredit;
    private HashMap<String, Course> codeToCourses;
    private ArrayList<Course> courses;
    private CourseService courseService;
    private DegreeService degreeService;
    private DegreeReqService degreeReqService;
    private PreRequisiteService preRequisiteService;
    private DisallowedCoursePairService disallowedCoursePairService;

    @Autowired
    public DegreeData(
            CourseService courseService,
            DegreeService degreeService,
            DegreeReqService degreeReqService,
            PreRequisiteService preRequisiteService,
            DisallowedCoursePairService disallowedCoursePairService) {
        this.numberOfCourses = 0;
        this.degree = new HashSet<String>();
        this.degreeToCourse = new HashMap<String, List<Course>>();
        this.degreeRequirements = new HashMap<String, HashSet<String>>();
        this.requirementToCredit = new HashMap<String, Integer>();
        this.codeToCourses = new HashMap<String, Course>();
        this.courses = new ArrayList<Course>();
        
        this.courseService = courseService;
        this.degreeService = degreeService;
        this.degreeReqService = degreeReqService;
        this.preRequisiteService = preRequisiteService;
        this.disallowedCoursePairService = disallowedCoursePairService;
    }
    
    public boolean loadAllCourse(){
        try {
            for (DegreeEntity degreeEntity : this.degreeService.getDegree()) {
                if (!loadDegreeCourse(degreeEntity.getName())) {
                    System.err.println("Failed to load for degree: "+degreeEntity.getName());
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return true;
    }

    public boolean loadDegreeCourse(String degree){
        //Read from database
        try {
            degree = degree.strip();
            degree = degree.toUpperCase();
            if(this.degree.contains(degree)){ //if we have loaded it before, return
                return true;
            }
            DegreeEntity degreeEntity = this.degreeService.selectDegreeByName(degree);
            Set<DegreeReqEntity> degreeReqEntities = degreeEntity.getDegreeReqEntities();
            Set<CourseEntity> courseEntities = degreeEntity.getCourseEntities();
            //Now we add the course to the hashmap for degree
            for(CourseEntity courseEntity: courseEntities){
                String courseCode = courseEntity.getCode();
                Course course = new Course(courseCode, courseEntity.getTitle(), courseEntity.getCredit(), null, null, null, null);
                //Try to retrive the old course for operation
                if(codeToCourses.containsKey(courseCode)){
                    course = codeToCourses.get(courseCode);
                }else{
                    this.codeToCourses.put(course.getCode(), course);
                    this.courses.add(course);
                }
                //Add this degree to course.major
                if(course.major==null){
                    course.major = new HashSet<String>();
                }
                course.major.add(degreeEntity.getName());

                for(DegreeReqEntity degreeReq: courseEntity.getDegreeReqEntity()){
                    if(degreeReq.getDegreeEntity().getName().equals(degree)){
                        if(course.majorSatisfiedCategories.containsKey(degree)){//if map already has the key
                            HashSet<String> oldSet = course.majorSatisfiedCategories.get(degree);
                            oldSet.add(degreeReq.getName());
                            course.majorSatisfiedCategories.replace(degree, oldSet);
                        }else{//if map doesn't have the key then we create a new one
                            HashSet<String> newSet = new HashSet<String>();
                            newSet.add(degreeReq.getName());
                            course.majorSatisfiedCategories.put(degree, newSet);
                        }
                    }
                }

                //input requisite into course
                Set<PreRequisiteGroupEntity> coursePreReqGroupEntities = courseEntity.getPreRequisiteGroupEntity();
                List<List<String>> preRequisites = new ArrayList<List<String>>();
                for(PreRequisiteGroupEntity coursePreReqGroupEntity: coursePreReqGroupEntities){
                    Set<PreRequisiteEntity> coursePreReqEntities = coursePreReqGroupEntity.getPreRequisiteEntities();
                    List<String> preRequisite = new ArrayList<String>();
                    for(PreRequisiteEntity coursePreReqEntity: coursePreReqEntities){
                        preRequisite.add(coursePreReqEntity.getCoursePreRequisiteEntity().getCode());
                    }
                    preRequisites.add(preRequisite);
                }
                course.setpreRequesits(preRequisites);

                //add degree to degreeToCourse hashmap
                if(this.degreeToCourse.containsKey(degree)){//If there's a list already in the hashmap
                    List<Course> currentDegreeCourseList = degreeToCourse.get(degree);
                    currentDegreeCourseList.add(course);
                    degreeToCourse.replace(degree, currentDegreeCourseList);
                }else{
                    List<Course> newDegreeCourseList = new ArrayList<Course>();
                    newDegreeCourseList.add(course);
                    degreeToCourse.put(degree, newDegreeCourseList);
                }

                //checks and add unique category to the dataset
                for(DegreeReqEntity degreeReqEntity: degreeReqEntities){ //loop through degreeRequirments and add it
                    if(!degreeRequirements.containsKey(degree)){
                        HashSet<String> requirements = new HashSet<String>();
                        requirements.add(degreeReqEntity.getName());
                        degreeRequirements.put(degree, requirements);
                    }else{
                        HashSet<String> requirements = degreeRequirements.get(degree);
                        requirements.add(degreeReqEntity.getName());
                        degreeRequirements.replace(degree, requirements);
                    }
                    
                    //requirement to credit hashmap set up
                    StringUtil stringUtil = new StringUtil();
                    String uniqueRequirementName = stringUtil.getUniqueDegreeCatString(degree, degreeReqEntity.getName());
                    if(!this.requirementToCredit.containsKey(uniqueRequirementName)){ //if not exist we add it
                        int requirementCredit = degreeReqEntity.getMinimumCredit();
                        this.requirementToCredit.put(uniqueRequirementName, requirementCredit);
                    }else{ //if exist, we checks if it is correct
                        int requirementCredit = this.requirementToCredit.get(uniqueRequirementName);
                        if(requirementCredit != degreeReqEntity.getMinimumCredit()) throw new IllegalStateException("Degree requirement minimum credits are not the same for: "+uniqueRequirementName+" in degree: "+degree);
                    }
                }

                //set the disallowed course
                Set<DisallowedCoursePairEntity> disallowedCourseEntities = disallowedCoursePairService.selectDisallowedCoursePairByCourse(courseEntity);
                Set<String> disallowedCourseCodes = new HashSet<String>();
                if(disallowedCourseEntities != null){
                    for(DisallowedCoursePairEntity disallowedCourseEntity: disallowedCourseEntities){
                        disallowedCourseCodes.add(courseService.selectCourse(disallowedCourseEntity.getDisallowedCourseEntity().getId()).getCode());
                    }
                }
                course.setDisallowedCourses(disallowedCourseCodes);
                this.numberOfCourses++;
                this.degree.add(degree); //this is a hashset so adding duplicate is fine
            }
            return true;
        } catch (Exception e) {
            // Handle file not found exception
            e.printStackTrace();
            return false;
        }
    }

    public HashSet<Course> filterCourseByPrefix(String prefix, int limit){
        HashSet<Course> filteredCourse = new HashSet<Course>();
        for(Course course:this.courses){
            if(course.code.startsWith(prefix)){
                boolean coursePreReqNotAdded= false;
                //add all pre requisite courses too 
                for(List<String> preReqList: course.preRequesites){
                    if(coursePreReqNotAdded) break;
                    for(String preReq: preReqList){
                        if(this.codeToCourses.get(preReq)==null){
                            coursePreReqNotAdded = true;
                            break;
                        }
                        filteredCourse.add(this.codeToCourses.get(preReq));
                    }
                }
                if(coursePreReqNotAdded) break;
                filteredCourse.add(course);
                if(limit > 0){
                    limit--;
                    if(limit ==0) break;
                }
            }
            
        }
        return filteredCourse;
    }

    public HashSet<Course> filterCourseByDegree(String degree, int limit){
        HashSet<Course> filteredCourse = new HashSet<Course>();
        String capitalizedDegree = degree.toUpperCase();
        for(Course course:this.degreeToCourse.get(capitalizedDegree)){
            if(course == null){
                System.err.println("Course is null for "+capitalizedDegree);
                continue;
            }
            filteredCourse.add(course);
            //add all pre requisite courses too 
            for(List<String> preReqList: course.preRequesites){
                for(String preReq: preReqList){
                    if(this.codeToCourses.get(preReq)==null){
                        System.err.println("Pre-requisite class for course: "+course.code+" named: "+preReq+" does not exist.");
                    }
                    filteredCourse.add(this.codeToCourses.get(preReq));
                }
            }
            if(limit > 0){
                limit--;
                if(limit ==0) break;
            }
            
        }
        return filteredCourse;
    }

    public int getDegreeNumberOfRequirement(String _degree) throws IllegalStateException{
        _degree = _degree.strip();
        _degree = _degree.toUpperCase();
        if(!this.degree.contains(_degree)) throw new IllegalStateException("This dataset does not contain degree: "+_degree);
        return this.degreeRequirements.get(_degree).size();
    }

    public int getTotalNumberOfRequirement(){
        int count = 0;
        for(HashMap.Entry<String, HashSet<String>> entry : this.degreeRequirements.entrySet()){
            count += entry.getValue().size();
        }

        return count;
    }
}
