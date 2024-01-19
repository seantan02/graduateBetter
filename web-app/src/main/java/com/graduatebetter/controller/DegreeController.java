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
import com.graduatebetter.service.PreRequisiteGroupService;
import com.graduatebetter.service.PreRequisiteService;
import com.graduatebetter.util.Course;
import com.graduatebetter.util.DegreeData;
import com.graduatebetter.util.DegreePathSearch;
import com.graduatebetter.util.StringUtil;

import jakarta.annotation.PostConstruct;

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

    private DegreeData degreeData;

    @PostConstruct
    private void init() {
        this.degreeData = new DegreeData(this.courseService, this.degreeService, this.degreeReqService, this.preRequisiteService);
    }

    @PostMapping("getAll")
    public List<DegreeEntity> getAllDegree() {
        return degreeService.getDegree();
    }
    
    @PostMapping("/getShortestPath")
    public ResponseEntity<List<String>> getShortestPath(@RequestBody DegreePathRequest request) {
        List<String> degreeIdStrings = request.getDegreeIds();
        Set<String> degreeNameSet = new HashSet<String>();
        if(request.getDegreeIds().size() <=1){ //error because it has to be more than 1
            System.err.println("Please provide more than 1 degree to compute the shortest path.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        for(String degreeIdString: degreeIdStrings){
            Long degreeId = Long.parseLong(degreeIdString);
            DegreeEntity degree = degreeService.selectDegree(degreeId);
            String degreeName = degree.getName();
            degreeName = degreeName.toUpperCase();
            degreeData.loadDegreeCourse(degreeName);
            degreeNameSet.add(degreeName);
        }

        if(degreeNameSet.size() != degreeIdStrings.size()){ //using size of set we can tell if original given list of string are unique, if not we reject it.
            System.err.println("Please provide atleast 2 unique degree ID to compute the shortest path.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        int numberOfDegreeRequirements = degreeData.getTotalNumberOfRequirement();
        System.out.println("Total number of degree requirements: "+numberOfDegreeRequirements);
        DegreePathSearch courseSearch = new DegreePathSearch(numberOfDegreeRequirements);
        
        for(String degreeName: degreeNameSet){
            for(Course _course: degreeData.filterCourseByDegree(degreeName, 99999)){
                courseSearch.addCourse(_course);
            }
        }

        HashMap<String, Integer> categoryToIndex = courseSearch.getCategoryToInt();
        System.out.println(categoryToIndex.size());
        int[] categoriesValues = new int[categoryToIndex.size()];
        StringUtil stringUtil = new StringUtil();
        for(String degreeName: degreeNameSet){
            for(String requirement: degreeData.getDegreeRequirements().get(degreeName)){
                String categoryName = stringUtil.getUniqueDegreeCatString(degreeName, requirement);

                int neededCredit = degreeData.getRequirementToCredit().get(categoryName);
                int index = -1;

                try{
                    System.out.println("Category name : "+categoryName);
                    index = categoryToIndex.get(categoryName);
                }catch(Exception e){
                    System.err.println(e);
                }
                //If no category found
                if(index == -1){
                    System.out.println("No classes have satisfy "+requirement+". Impossible.");
                    break;
                }
                categoriesValues[index] = neededCredit;
            }
        }

        courseSearch.setStartState(categoriesValues); //set the start state
        int[] targetState = new int[categoriesValues.length]; 
        courseSearch.setTargetState(targetState); //set the end goal (all zeros)
        
        List<List<int[]>> allCreComPath = courseSearch.computeShortestPath(); //This computes the big paths
        List<List<int[]>> bestCreComPath = new ArrayList<List<int[]>>(); //The best path we computed
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
                List<Course> bestCourseToTake = courseSearch.computeBestCourseFromPath(bestCreComList);
                
                for(Course bestCourse: bestCourseToTake){
                    bestTotalCredit+=bestCourse.credits;
                }

                if(smallestSize > bestCourseToTake.size() || smallestCredit > bestTotalCredit){
                    smallestSize = bestCourseToTake.size();
                    smallestCredit = bestTotalCredit;
                    finalBestCourseToTake = bestCourseToTake;
                    System.out.println("New smallest size/credit found: "+smallestSize+", "+smallestCredit);
                }
            }
        }

        List<String> bestCourseCodeToTake = new ArrayList<String>();
        for(Course bestCourse: finalBestCourseToTake){
            bestCourseCodeToTake.add(bestCourse.code);
        }
        Collections.sort(bestCourseCodeToTake); //sort it in alphabetical order
        return new ResponseEntity<List<String>>(bestCourseCodeToTake, HttpStatus.CREATED);
    }

    @PostMapping("/getSmallestCredit")
    public ResponseEntity<Integer> getSmallestCredit(@RequestBody DegreePathRequest request) {
        List<String> degreeIdStrings = request.getDegreeIds();
        Set<String> degreeNameSet = new HashSet<String>();
        if(request.getDegreeIds().size() <=1){ //error because it has to be more than 1
            System.err.println("Please provide more than 1 degree to compute the shortest path.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        for(String degreeIdString: degreeIdStrings){
            Long degreeId = Long.parseLong(degreeIdString);
            DegreeEntity degree = degreeService.selectDegree(degreeId);
            String degreeName = degree.getName();
            degreeName = degreeName.toUpperCase();
            degreeData.loadDegreeCourse(degreeName);
            degreeNameSet.add(degreeName);
        }

        if(degreeNameSet.size() != degreeIdStrings.size()){ //using size of set we can tell if original given list of string are unique, if not we reject it.
            System.err.println("Please provide atleast 2 unique degree ID to compute the shortest path.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        int numberOfDegreeRequirements = degreeData.getTotalNumberOfRequirement();
        DegreePathSearch courseSearch = new DegreePathSearch(numberOfDegreeRequirements);
        
        for(String degreeName: degreeNameSet){
            for(Course _course: degreeData.filterCourseByDegree(degreeName, 99999)){
                courseSearch.addCourse(_course);
            }
        }

        HashMap<String, Integer> categoryToIndex = courseSearch.getCategoryToInt();
        System.out.println(categoryToIndex.size());
        int[] categoriesValues = new int[categoryToIndex.size()];
        StringUtil stringUtil = new StringUtil();
        for(String degreeName: degreeNameSet){
            for(String requirement: degreeData.getDegreeRequirements().get(degreeName)){
                String categoryName = stringUtil.getUniqueDegreeCatString(degreeName, requirement);

                int neededCredit = degreeData.getRequirementToCredit().get(categoryName);
                int index = -1;

                try{
                    System.out.println("Category name : "+categoryName);
                    index = categoryToIndex.get(categoryName);
                }catch(Exception e){
                    System.err.println(e);
                }
                //If no category found
                if(index == -1){
                    System.out.println("No classes have satisfy "+requirement+". Impossible.");
                    break;
                }
                categoriesValues[index] = neededCredit;
            }
        }

        courseSearch.setStartState(categoriesValues); //set the start state
        int[] targetState = new int[categoriesValues.length]; 
        courseSearch.setTargetState(targetState); //set the end goal (all zeros)
        
        List<List<int[]>> finalBestCreComPath = new ArrayList<List<int[]>>(); //The best path we computed
        int currLowestPathSize = Integer.MAX_VALUE;
        for(int repeat = 1;repeat>0;repeat--){ //repeat 3 times because for some reason best path was not constant
            List<List<int[]>> allCreComPath = courseSearch.computeShortestPath(); //This computes the big paths
            List<List<int[]>> bestCreComPath = new ArrayList<List<int[]>>(); //The best path we computed
            int i = allCreComPath.size()-1; //Set i as the final (When we reach end goal)
            while(i>=0){// when i less than 0 means we get to the start state so stop
                List<int[]> creComPath = allCreComPath.get(i);
                int parentIndex = creComPath.get(1)[0]; //Get the parent index and trace it back
                bestCreComPath.add(creComPath);
                i=parentIndex;
            }
            //compare and update lowest value
            if(currLowestPathSize > bestCreComPath.size()){
                currLowestPathSize = bestCreComPath.size();
                finalBestCreComPath = bestCreComPath;
            }
        }

        List<int[]> bestCreComList = new ArrayList<int[]>(); //The best moves to take in String
        for(int index = finalBestCreComPath.size()-2;index>=0;index--){ //start from the second last (size-2) because we want to skip move of all zeroes
            int[] parentMove = finalBestCreComPath.get(index).get(2);
            bestCreComList.add(parentMove);
        }

        int smallestCredit = Integer.MAX_VALUE;
        Set<List<int[]>> visitedCreComArrangements = new HashSet<List<int[]>>();
        for(int repeat=3000;repeat>0;repeat--){
            int bestTotalCredit = 0;
            Collections.shuffle(bestCreComList);
            if(visitedCreComArrangements.add(bestCreComList)){
                List<Course> bestCourseToTake = courseSearch.computeBestCourseFromPath(bestCreComList);
                
                for(Course bestCourse: bestCourseToTake){
                    bestTotalCredit+=bestCourse.credits;
                }

                if(smallestCredit > bestTotalCredit){
                    smallestCredit = bestTotalCredit;
                    System.out.println("New smallest credit found: "+smallestCredit);
                }
            }
        }
        
        return new ResponseEntity<Integer>(smallestCredit, HttpStatus.CREATED);
    }
}