package com.graduatebetter.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduatebetter.dto.DegreePathRequest;
import com.graduatebetter.model.DegreeEntity;
import com.graduatebetter.service.CourseService;
import com.graduatebetter.service.DegreeReqService;
import com.graduatebetter.service.DegreeService;
import com.graduatebetter.service.DisallowedCoursePairService;
import com.graduatebetter.service.PreRequisiteGroupService;
import com.graduatebetter.service.PreRequisiteService;
import com.graduatebetter.util.Course;
import com.graduatebetter.util.DegreeData;
import com.graduatebetter.util.DegreePathSearch;
import com.graduatebetter.util.StringUtil;

@RestController
@RequestMapping("/api/v1/degree")
public class DegreeController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private DegreeService degreeService;
    
    @Autowired
    private DegreeReqService degreeReqService;

    @Autowired
    private PreRequisiteService preRequisiteService;

    @Autowired
    private PreRequisiteGroupService preRequisiteGroupService;

    @Autowired
    private DisallowedCoursePairService disallowedCoursePairService;

    @Autowired
    private DegreePathSearch degreePathSearch;

    private DegreeData degreeData;

    @PostMapping("/getAll")
    public ResponseEntity<List<DegreeEntity>> getAllDegrees() {
        List<DegreeEntity> degrees = degreeService.getDegree();
    
        if (degrees.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(degrees, HttpStatus.OK);
        }
    }
    
    @PostMapping("/getShortestPath")
    public ResponseEntity<List<String>> getShortestPath(@RequestBody DegreePathRequest request) {
        if(this.degreeData == null){ //to initiate it once and for all
            this.degreeData = new DegreeData(this.courseService, this.degreeService, this.degreeReqService, this.preRequisiteService, this.disallowedCoursePairService);
        }

        if(this.degreeData.getCourses().size() ==0 || this.degreeData.getDegree().size() == 0){
            this.degreeData.loadAllCourse();
        }

        List<String> degreeIdStrings = request.getDegreeIds();
        Set<String> degreeNameSet = new HashSet<String>();
        if(request.getDegreeIds().size() <=1){ //error because it has to be more than 1
            System.err.println("Please provide more than 1 degree to compute the shortest path.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        int numberOfDegreeRequirements = 0;

        for(String degreeIdString: degreeIdStrings){
            Long degreeId = Long.parseLong(degreeIdString);
            DegreeEntity degree = degreeService.selectDegree(degreeId);
            String degreeName = degree.getName();
            degreeName = degreeName.toUpperCase();
            degreeData.loadDegreeCourse(degreeName);
            degreeNameSet.add(degreeName);
            numberOfDegreeRequirements += this.degreeData.getDegreeNumberOfRequirement(degreeName);
        }

        if(degreeNameSet.size() != degreeIdStrings.size()){ //using size of set we can tell if original given list of string are unique, if not we reject it.
            System.err.println("Please provide atleast 2 unique degree ID to compute the shortest path.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        this.degreePathSearch.setNumberOfCategories(numberOfDegreeRequirements);
        this.degreePathSearch.setSetOfDegree(degreeNameSet);

        for(String degreeName: degreeNameSet){
            for(Course _course: degreeData.filterCourseByDegree(degreeName, 99999)){
                this.degreePathSearch.addCourse(_course);
            }
        }

        HashMap<String, Integer> categoryToIndex = this.degreePathSearch.getCategoryToInt();
        int[] categoriesValues = new int[numberOfDegreeRequirements];
        StringUtil stringUtil = new StringUtil();
        for(String degreeName: degreeNameSet){
            for(String requirement: this.degreeData.getDegreeRequirements().get(degreeName)){
                String categoryName = stringUtil.getUniqueDegreeCatString(degreeName, requirement);

                int neededCredit = this.degreeData.getRequirementToCredit().get(categoryName);
                int index = -1;

                try{
                    index = categoryToIndex.get(categoryName);
                }catch(Exception e){
                    System.err.println(e);
                }
                //If no category found
                if(index == -1){
                    System.err.println("No classes have satisfy "+requirement+". Impossible.");
                    break;
                }
                categoriesValues[index] = neededCredit;
            }
        }

        this.degreePathSearch.setStartState(categoriesValues); //set the start state
        int[] targetState = new int[categoriesValues.length]; 
        this.degreePathSearch.setTargetState(targetState); //set the end goal (all zeros)
        
        try{
            List<List<int[]>>  allCreComPath = this.degreePathSearch.computeShortestPath().get(); //This computes the big paths
            List<List<int[]>> bestCreComPath =new ArrayList<List<int[]>>(); //The best path we computed
            List<int[]> bestCreComList = new ArrayList<int[]>(); //The best moves to take in String
            int i = allCreComPath.size()-1; //Set i as the final (When we reach end goal)
            while(i>=0){// when i less than 0 means we get to the start state so stop
                List<int[]> creComPath = allCreComPath.get(i);
                int parentIndex = creComPath.get(1)[0]; //Get the parent index and trace it back
                bestCreComPath.add(creComPath);
                i=parentIndex;
            }
            
            for(int index = bestCreComPath.size()-2;index>=0;index--){ //start from the second last (size-2) because we want to skip move of all zeroes
                int[] parentMove = bestCreComPath.get(index).get(2);
                bestCreComList.add(parentMove);
            }

            int smallestSize = Integer.MAX_VALUE;
            int smallestCredit = Integer.MAX_VALUE;
            List<Course> finalBestCourseToTake = new ArrayList<Course>();
            Set<List<int[]>> visitedCreComArrangements = new HashSet<List<int[]>>();
            for(int repeat=3000;repeat>0;repeat--){
                int bestTotalCredit = 0;
                Collections.shuffle(bestCreComList);
                if(visitedCreComArrangements.add(bestCreComList)){
                    try{
                        List<Course> bestCourseToTake = this.degreePathSearch.computeBestCourseFromPath(bestCreComList).get();
                    
                        for(Course bestCourse: bestCourseToTake){
                            bestTotalCredit+=bestCourse.credits;
                        }

                        if(smallestSize > bestCourseToTake.size() || smallestCredit > bestTotalCredit){
                            smallestSize = bestCourseToTake.size();
                            smallestCredit = bestTotalCredit;
                            finalBestCourseToTake = bestCourseToTake;
                            System.out.println("New smallest size/credit found: "+smallestSize+", "+smallestCredit);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    }
                }
            }
            List<String> bestCourseCodeToTake = new ArrayList<String>();
            for(Course bestCourse: finalBestCourseToTake){
                bestCourseCodeToTake.add(bestCourse.code+", credits: "+bestCourse.credits);
            }
            Collections.sort(bestCourseCodeToTake); //sort it in alphabetical order
            return new ResponseEntity<List<String>>(bestCourseCodeToTake, HttpStatus.CREATED);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/getSmallestCredit")
    public ResponseEntity<Integer> getSmallestCredit(@RequestBody DegreePathRequest request) {
        if(this.degreeData == null){ //to initiate it once and for all
            this.degreeData = new DegreeData(this.courseService, this.degreeService, this.degreeReqService, this.preRequisiteService, this.disallowedCoursePairService);
        }

        if(this.degreeData.getCourses().size() ==0 || this.degreeData.getDegree().size() == 0){
            this.degreeData.loadAllCourse();
        }

        List<String> degreeIdStrings = request.getDegreeIds();
        Set<String> degreeNameSet = new HashSet<String>();
        if(request.getDegreeIds().size() <=1){ //error because it has to be more than 1
            System.err.println("Please provide more than 1 degree to compute the shortest path.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        int numberOfDegreeRequirements = 0;

        for(String degreeIdString: degreeIdStrings){
            Long degreeId = Long.parseLong(degreeIdString);
            DegreeEntity degree = degreeService.selectDegree(degreeId);
            String degreeName = degree.getName();
            degreeName = degreeName.toUpperCase();
            degreeData.loadDegreeCourse(degreeName);
            degreeNameSet.add(degreeName);
            numberOfDegreeRequirements += this.degreeData.getDegreeNumberOfRequirement(degreeName);
        }

        if(degreeNameSet.size() != degreeIdStrings.size()){ //using size of set we can tell if original given list of string are unique, if not we reject it.
            System.err.println("Please provide atleast 2 unique degree ID to compute the shortest path.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        DegreePathSearch courseSearch = new DegreePathSearch(numberOfDegreeRequirements, degreeNameSet);

        for(String degreeName: degreeNameSet){
            for(Course _course: degreeData.filterCourseByDegree(degreeName, 99999)){
                courseSearch.addCourse(_course);
            }
        }

        HashMap<String, Integer> categoryToIndex = courseSearch.getCategoryToInt();
        int[] categoriesValues = new int[numberOfDegreeRequirements];
        StringUtil stringUtil = new StringUtil();
        for(String degreeName: degreeNameSet){
            for(String requirement: this.degreeData.getDegreeRequirements().get(degreeName)){
                String categoryName = stringUtil.getUniqueDegreeCatString(degreeName, requirement);

                int neededCredit = this.degreeData.getRequirementToCredit().get(categoryName);
                int index = -1;

                try{
                    index = categoryToIndex.get(categoryName);
                }catch(Exception e){
                    System.err.println(e);
                }
                //If no category found
                if(index == -1){
                    System.err.println("No classes have satisfy "+requirement+". Impossible.");
                    break;
                }
                categoriesValues[index] = neededCredit;
            }
        }

        courseSearch.setStartState(categoriesValues); //set the start state
        int[] targetState = new int[categoriesValues.length]; 
        courseSearch.setTargetState(targetState); //set the end goal (all zeros)
        
        try{
            List<List<int[]>>  allCreComPath = this.degreePathSearch.computeShortestPath().get(); //This computes the big paths
            List<List<int[]>> bestCreComPath = new ArrayList<List<int[]>>(); //The best path we computed
            int i = allCreComPath.size()-1; //Set i as the final (When we reach end goal)
            while(i>=0){// when i less than 0 means we get to the start state so stop
                List<int[]> creComPath = allCreComPath.get(i);
                int parentIndex = creComPath.get(1)[0]; //Get the parent index and trace it back
                bestCreComPath.add(creComPath);
                i=parentIndex;
            }

            List<int[]> bestCreComList = new ArrayList<int[]>(); //The best moves to take in String
            for(int index = bestCreComPath.size()-2;index>=0;index--){ //start from the second last (size-2) because we want to skip move of all zeroes
                int[] parentMove = bestCreComPath.get(index).get(2);
                bestCreComList.add(parentMove);
            }

            int smallestCredit = Integer.MAX_VALUE;
            Set<List<int[]>> visitedCreComArrangements = new HashSet<List<int[]>>();
            for(int repeat=3000;repeat>0;repeat--){
                int bestTotalCredit = 0;
                Collections.shuffle(bestCreComList);
                if(visitedCreComArrangements.add(bestCreComList)){
                    try{
                        List<Course> bestCourseToTake = this.degreePathSearch.computeBestCourseFromPath(bestCreComList).get();
                    
                        for(Course bestCourse: bestCourseToTake){
                            bestTotalCredit+=bestCourse.credits;
                        }

                        if(smallestCredit > bestTotalCredit){
                            smallestCredit = bestTotalCredit;
                            System.out.println("New smallest credit found: "+smallestCredit);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                    }
                }
            }
            
            return new ResponseEntity<Integer>(smallestCredit, HttpStatus.CREATED);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}