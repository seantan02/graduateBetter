package com.graduatebetter.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
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
import com.graduatebetter.service.PreRequisiteGroupService;
import com.graduatebetter.service.PreRequisiteService;
import io.jsonwebtoken.io.IOException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;

@RequestMapping("/api/v1")
@RestController
public class MainController {

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

    @Value("classpath:data/course/*")
    private Resource[] courseDatasetResources;

    @Value("classpath:data/degree/*")
    private Resource[] degreeDatasetResources;

    @Value("${app.name}")
    private String appName;
    
    @Value("${app.version}")
    private String version;

    @RequestMapping(value = "/version", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String handleCombinedRequest() {
        return "version: "+version;
    }
    
    @PostMapping("/readAllCourseData")
    public ResponseEntity<String> readAllCourseData() {
        try{
            for(Resource resource:courseDatasetResources){//readEachFile and run the algorithm
                try {
                    // Get the InputStream
                    InputStream inputStream = resource.getInputStream();

                    // Read data from the InputStream
                    // For example, you can read the content line by line
                    try (java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A")) {
                        try{
                            HashMap<Long, List<List<String>>> preRequisiteToAddAfter = new HashMap<Long, List<List<String>>>();
                            HashMap<Long, List<PreRequisiteGroupEntity>> preRequisiteGroupToBeUse = new HashMap<Long, List<PreRequisiteGroupEntity>>();
                            HashMap<Long, List<String>> disallowedCoursePairToAddAfter = new HashMap<Long, List<String>>();
                            if (scanner.hasNextLine()) {
                                scanner.nextLine(); // Skip the header
                            }

                            while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                ArrayList<String> lineDetails = new ArrayList<String>();
                                //We begin to look for " and break the line into pieces
                                boolean insideQuotes = false;
                                String lineDetail = "";
                                for(int i=0;i<line.length();i++){
                                    char currentChar = line.charAt(i);
                                    //If char == " then we want to avoid any , until next "
                                    if (currentChar == '\"') {
                                        insideQuotes = !insideQuotes;
                                    }else if (currentChar == ',' && !insideQuotes) {
                                        // Add the current element to the result list
                                        lineDetails.add(lineDetail.trim());
                                        // Reset the StringBuilder for the next element
                                        lineDetail = "";
                                    } else {
                                        // Append the character to the current element
                                        lineDetail+=currentChar;
                                    }
                                    //If this is last loop and lineDetail not empty then we need to add it
                                    if(i+1 == line.length() && lineDetail.length()>=0){
                                        // Add the current element to the result list
                                        lineDetails.add(lineDetail.trim());
                                        // Reset the StringBuilder for the next element
                                        lineDetail = "";
                                    }
                                }
                                //Asumming we have index 0 as class course
                                //index 1 is the number of requisite
                                //index > 1 is all the requisite
                                //check if course with same code already exist
                                CourseEntity courseEntity = new CourseEntity();
                                DegreeEntity degreeEntity = new DegreeEntity();
                                //First column: Class code
                                String classCode = lineDetails.get(0);
                                classCode = classCode.trim();
                                classCode = classCode.toUpperCase();
                                if(this.courseService.courseExistByCode(classCode)){
                                    courseEntity = this.courseService.selectCourseByCode(classCode);
                                }else{
                                    courseEntity.setCode(classCode);
                                }
                                //Second Column: Title
                                String classTitle = lineDetails.get(1);
                                classTitle = classTitle.trim();
                                classTitle = classTitle.toUpperCase();
                                if(courseEntity.getTitle() == null) courseEntity.setTitle(classTitle); //only update if the course is new (title = "")
                                //Third Column: Credits
                                int credits = Integer.parseInt(lineDetails.get(2));
                                if(courseEntity.getCredit() == 0){ //if new course (credit == 0 ) then update
                                    courseEntity.setCredit(credits);
                                }else{//check if previous course has different credit, if yes, throw exception
                                    if(courseEntity.getCredit() != credits) throw new IllegalStateException("Course with code: "+courseEntity.getCode()+" has a duplicate that has different credit. Previous: "+courseEntity.getCredit()+". New: "+credits);
                                }
                                //Fourth Column: Major
                                String degree = lineDetails.get(3);
                                degree = degree.trim();
                                degree = degree.toUpperCase();
                                if(degreeService.degreeExistByName(degree)) degreeEntity = degreeService.selectDegreeByName(degree);//it's a hashset so it wont have duplicate
                                degreeEntity.setName(degree);
                                degreeEntity.getCourseEntities().add(courseEntity);
                                courseEntity.getDegreeEntity().add(degreeEntity);
                                if(courseEntity.getId()==null) courseEntity = courseService.createCourse(courseEntity); // save degree
                                if(degreeEntity.getId()==null) degreeEntity = degreeService.createDegree(degreeEntity); // save degree
                                //Fifth Column:  satisfied category
                                if(lineDetails.size() >= 4 && !lineDetails.get(4).equals("")){ //Only perform if category is not empty
                                    String[] satisfiedCatList = lineDetails.get(4).split("&");
                                    for(String satisfiedCatElem: satisfiedCatList){
                                        DegreeReqEntity degreeReqEntity = new DegreeReqEntity();
                                        satisfiedCatElem = satisfiedCatElem.trim();
                                        satisfiedCatElem = satisfiedCatElem.toUpperCase();
                                        boolean satisfiedCatAlreadyAdded = false;
                                        for(DegreeReqEntity degreeReq: degreeEntity.getDegreeReqEntities()){
                                            if(degreeReq.getName() != null && degreeReq.getName().equals(satisfiedCatElem)){
                                                degreeReqEntity = degreeReq;
                                                satisfiedCatAlreadyAdded = true;
                                                break;
                                            }
                                        }
                                        degreeReqEntity.getCourseEntities().add(courseEntity);
                                        courseEntity.getDegreeReqEntity().add(degreeReqEntity);
                                        if(!satisfiedCatAlreadyAdded){
                                            degreeReqEntity.setName(satisfiedCatElem);
                                            degreeReqEntity.setMinimumCredit(15);
                                            degreeReqEntity.setDegreeEntity(degreeEntity);
                                            degreeEntity.getDegreeReqEntities().add(degreeReqEntity);
                                            degreeReqService.createDegreeReq(degreeReqEntity);
                                        }
                                    }
                                }
                                //Sixth column: Pre Requisites
                                if(lineDetails.size() >= 6 && !lineDetails.get(5).equals("")){
                                    String[] splitByAnd = lineDetails.get(5).split("&");
                                    for(String splitByAndPart: splitByAnd){
                                        String[] requisites = splitByAndPart.split(","); //group of requisites
                                        PreRequisiteGroupEntity currPreRequisiteGroup = new PreRequisiteGroupEntity();
                                        List<String> unaddedPreReq = new ArrayList<String>();
                                        for(String requisite: requisites){
                                            requisite = requisite.replaceAll("[{}]", "");
                                            requisite = requisite.trim();
                                            requisite = requisite.toUpperCase();
                                            boolean requisiteAlreadyAdded = false;
                                            /*
                                             * 1. Check if this course already has this requisite added, skip if yes
                                             * 2. Add if not
                                             */
                                            if(courseEntity.getId()==null){
                                                requisiteAlreadyAdded = true;
                                                break;
                                            }else if(preRequisiteService.preRequisiteExist(courseEntity)){ //check if this course has existing requisites in DB
                                                Set<PreRequisiteGroupEntity> preRequisiteGroups = courseEntity.getPreRequisiteGroupEntity();
                                                for(PreRequisiteGroupEntity preRequisiteGroup: preRequisiteGroups){
                                                    Set<PreRequisiteEntity> preRequisiteEntities = preRequisiteGroup.getPreRequisiteEntities();
                                                    for(PreRequisiteEntity preRequisiteEntityInDB: preRequisiteEntities){ //Check if existing database has the current requisite we are looking at
                                                        if(preRequisiteEntityInDB.getCoursePreRequisiteEntity().getCode().equals(requisite)){ //if yes, we set boolean to true and break
                                                            currPreRequisiteGroup = preRequisiteGroup;
                                                            requisiteAlreadyAdded = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            if(!requisiteAlreadyAdded){ //if the current requisite we are looking at is new then we will
                                                PreRequisiteEntity preRequisiteEntity = new PreRequisiteEntity();
                                                if(courseService.courseExistByCode(requisite)){ //we set it now
                                                    CourseEntity preRequisiteCourse = courseService.selectCourseByCode(requisite);
                                                    preRequisiteEntity.setCoursePreRequisiteEntity(preRequisiteCourse);
                                                    preRequisiteEntity.setPreRequisiteGroupEntity(currPreRequisiteGroup);
                                                    currPreRequisiteGroup.setCourseEntity(courseEntity);
                                                    currPreRequisiteGroup.getPreRequisiteEntities().add(preRequisiteEntity);
                                                    courseEntity.getPreRequisiteGroupEntity().add(currPreRequisiteGroup);
                                                    courseEntity.getPreRequisiteEntity().add(preRequisiteEntity);
                                                    if(currPreRequisiteGroup.getId()==null) currPreRequisiteGroup = preRequisiteGroupService.createPreRequisiteGroup(currPreRequisiteGroup);//save group first
                                                    preRequisiteService.createPreRequisite(preRequisiteEntity);
                                                }else{//we add it to to-do-list and do it later
                                                    unaddedPreReq.add(requisite);
                                                }
                                            }
                                        }
                                        //Create or Add the pre-requisite group to be used later to add unadded pre-requisite
                                        if(preRequisiteGroupToBeUse.containsKey(courseEntity.getId())){
                                            List<List<String>> unaddedPreReqList = preRequisiteToAddAfter.get(courseEntity.getId());
                                            unaddedPreReqList.add(unaddedPreReq);
                                            preRequisiteToAddAfter.replace(courseEntity.getId(), unaddedPreReqList);
                                        }else{
                                            List<List<String>> unaddedPreReqList = new ArrayList<List<String>>();
                                            unaddedPreReqList.add(unaddedPreReq);
                                            preRequisiteToAddAfter.put(courseEntity.getId(), unaddedPreReqList);
                                        }
                                        //Create or Add the pre-requisite group to be used later to add unadded pre-requisite
                                        if(preRequisiteGroupToBeUse.containsKey(courseEntity.getId())){
                                            List<PreRequisiteGroupEntity> preReqToUseList = preRequisiteGroupToBeUse.get(courseEntity.getId());
                                            preReqToUseList.add(currPreRequisiteGroup);
                                            preRequisiteGroupToBeUse.replace(courseEntity.getId(), preReqToUseList);
                                        }else{
                                            List<PreRequisiteGroupEntity> preReqToUseList = new ArrayList<PreRequisiteGroupEntity>();
                                            preReqToUseList.add(currPreRequisiteGroup);
                                            preRequisiteGroupToBeUse.put(courseEntity.getId(), preReqToUseList);
                                        }
                                    }
                                }
                                //Seventh column: Disallowed Courses
                                if(lineDetails.size() >= 7 && !lineDetails.get(6).equals("")){
                                    String[] disallowedCourseCodes = lineDetails.get(6).split(","); //all of the disallowed course codes
                                    Set<DisallowedCoursePairEntity> existingDisallowedCoursePairs = disallowedCoursePairService.selectDisallowedCoursePairByCourse(courseEntity); //select what current course has in database table "disallowed_course_pair"
                                    for(String disallowedCourseCode: disallowedCourseCodes){
                                        disallowedCourseCode = disallowedCourseCode.replaceAll("[{}]", "");
                                        disallowedCourseCode = disallowedCourseCode.trim();
                                        disallowedCourseCode = disallowedCourseCode.toUpperCase();
                                        boolean disallowedCoursePairNotExist = true;
                                        /*
                                         * 1. For each disallowed course code, we select the course entity
                                         * 2. If it exist then we check if it exist in our table "disallowed_course_pair"
                                         * 3. If not exist, we add it
                                         * 4. Skip if it already exists
                                         * 5. If the disallowed course is not in our database yet, we add it later
                                         */
                                        CourseEntity disallowedCourseEntity = courseService.selectCourseByCode(disallowedCourseCode); //get the disallowed course
                                        if(disallowedCourseEntity != null){ //if it exists in course table
                                            if(existingDisallowedCoursePairs != null){
                                                for(DisallowedCoursePairEntity existingDisallowedCoursePair: existingDisallowedCoursePairs){
                                                    if(existingDisallowedCoursePair.getDisallowedCourseEntity().equals(disallowedCourseEntity)){
                                                        disallowedCoursePairNotExist = false;
                                                        break;
                                                    }
                                                }
                                            }

                                            if(disallowedCoursePairNotExist){ //if not exist in database and both course is available, we add it to database
                                                DisallowedCoursePairEntity disallowedCoursePairEntity = new DisallowedCoursePairEntity();
                                                disallowedCoursePairEntity.setCourseEntity(courseEntity);
                                                disallowedCoursePairEntity.setDisallowedCourseEntity(disallowedCourseEntity);
                                                disallowedCoursePairService.createDisallowedCoursePair(disallowedCoursePairEntity);
                                            }
                                        }else{ //create or add this code to the hashmap that we will go through after reading the entire file so we don't miss any
                                            if(disallowedCoursePairToAddAfter.containsKey(courseEntity.getId())){
                                                List<String> oldList = disallowedCoursePairToAddAfter.get(courseEntity.getId());
                                                oldList.add(disallowedCourseCode);
                                                disallowedCoursePairToAddAfter.replace(courseEntity.getId(), oldList);
                                            }else{
                                                List<String> newList = new ArrayList<String>();
                                                newList.add(disallowedCourseCode);
                                                disallowedCoursePairToAddAfter.put(courseEntity.getId(), newList);
                                            }
                                        }
                                    }
                                }
                                courseEntity = courseService.updateCourse(courseEntity);
                                degreeEntity = degreeService.updateDegree(degreeEntity);
                            }
                            scanner.close();
                            //now we add the prequisite that are not added because the requisite course was not yet added to the database when we were looking at it
                            for(HashMap.Entry<Long, List<List<String>>> entry: preRequisiteToAddAfter.entrySet()){
                                Long key = entry.getKey();
                                List<List<String>> unaddedPreReqLists = entry.getValue();
                                List<PreRequisiteGroupEntity> unaddedPreReqGroups = preRequisiteGroupToBeUse.get(key);
                                CourseEntity courseEntity = courseService.selectCourse(key);
                                for(int i=0;i<unaddedPreReqLists.size();i++){
                                    List<String> unaddedPreReqList = unaddedPreReqLists.get(i);
                                    PreRequisiteGroupEntity unaddedPreReqGroup = unaddedPreReqGroups.get(i);
                                    for(String requisite: unaddedPreReqList){
                                        boolean requisiteAlreadyAdded = false;
                                        /*
                                        * 1. Check if this course already has this requisite added, skip if yes
                                        * 2. Add if not
                                        */
                                        if(preRequisiteService.preRequisiteExist(courseEntity)){ //check if this course has existing requisites in DB
                                            Set<PreRequisiteGroupEntity> preRequisiteGroups = courseEntity.getPreRequisiteGroupEntity();
                                            for(PreRequisiteGroupEntity preRequisiteGroup: preRequisiteGroups){
                                                Set<PreRequisiteEntity> preRequisiteEntities = preRequisiteGroup.getPreRequisiteEntities();
                                                for(PreRequisiteEntity preRequisiteEntityInDB: preRequisiteEntities){ //Check if existing database has the current requisite we are looking at
                                                    if(preRequisiteEntityInDB.getCoursePreRequisiteEntity().getCode().equals(requisite)){ //if yes, we set boolean to true and break
                                                        requisiteAlreadyAdded = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        if(!requisiteAlreadyAdded){ //if the current requisite we are looking at is new then we will add it
                                            PreRequisiteEntity preRequisiteEntity = new PreRequisiteEntity();
                                            if(courseService.courseExistByCode(requisite)){ //we set it now
                                                CourseEntity preRequisiteCourse = courseService.selectCourseByCode(requisite);
                                                preRequisiteEntity.setCoursePreRequisiteEntity(preRequisiteCourse);
                                                preRequisiteEntity.setPreRequisiteGroupEntity(unaddedPreReqGroup);
                                                unaddedPreReqGroup.setCourseEntity(courseEntity);
                                                unaddedPreReqGroup.getPreRequisiteEntities().add(preRequisiteEntity);
                                                courseEntity.getPreRequisiteGroupEntity().add(unaddedPreReqGroup);
                                                courseEntity.getPreRequisiteEntity().add(preRequisiteEntity);
                                                if(unaddedPreReqGroup.getId()==null) preRequisiteGroupService.createPreRequisiteGroup(unaddedPreReqGroup);//save group first
                                                preRequisiteService.createPreRequisite(preRequisiteEntity);
                                            }else{
                                                System.err.println("Course for requisite: "+requisite+" doesn't exist");
                                            }
                                        }
                                    }
                                }
                            }
                            //now we add the disallowed course pair that are not added because the course was not yet added to the database when we were looking at it
                            for(HashMap.Entry<Long, List<String>> entry: disallowedCoursePairToAddAfter.entrySet()){
                                Long key = entry.getKey();
                                List<String> unaddedDisallowedCourseLists = entry.getValue();
                                CourseEntity courseEntity = courseService.selectCourse(key);
                                Set<DisallowedCoursePairEntity> existingDisallowedCoursePairs = disallowedCoursePairService.selectDisallowedCoursePairByCourse(courseEntity);
                                for(int i=0;i<unaddedDisallowedCourseLists.size();i++){
                                    String unaddedDisallowedCourseCode = unaddedDisallowedCourseLists.get(i);
                                    CourseEntity unaddedDisallowedCourseEntity= courseService.selectCourseByCode(unaddedDisallowedCourseCode);
                                    boolean disallowedCoursePairNotExist = true;

                                    if(unaddedDisallowedCourseEntity != null){ //if it exists in course table
                                        if(existingDisallowedCoursePairs != null){
                                            for(DisallowedCoursePairEntity existingDisallowedCoursePair: existingDisallowedCoursePairs){
                                                if(existingDisallowedCoursePair.getDisallowedCourseEntity().equals(unaddedDisallowedCourseEntity)){
                                                    disallowedCoursePairNotExist = false;
                                                    break;
                                                }
                                            }
                                        }
                                        
                                        if(disallowedCoursePairNotExist){ //if not exist in database and both course is available, we add it to database
                                            DisallowedCoursePairEntity disallowedCoursePairEntity = new DisallowedCoursePairEntity();
                                            disallowedCoursePairEntity.setCourseEntity(courseEntity);
                                            disallowedCoursePairEntity.setDisallowedCourseEntity(unaddedDisallowedCourseEntity);
                                            disallowedCoursePairService.createDisallowedCoursePair(disallowedCoursePairEntity);
                                        }
                                    }else{
                                        throw new IllegalStateException("Unadded course pair still doesn't exist after scanning all files: "+unaddedDisallowedCourseCode);
                                    }
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    // You can perform other operations with the InputStream as needed

                } catch (IOException e) {
                    // Handle exceptions, e.g., file not found, etc.
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
                }
            }
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Accepted");
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
        }
    }
    @PostMapping("/readAllDegreeReq")
    public ResponseEntity<String> readAllDegreeReq() {
        try{
            for(Resource resource:degreeDatasetResources){//readEachFile and run the algorithm
                try {
                    // Get the InputStream
                    InputStream inputStream = resource.getInputStream();

                    // Read data from the InputStream
                    // For example, you can read the content line by line
                    try (java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A")) {
                        try{
                            if (scanner.hasNextLine()) {
                                scanner.nextLine(); // Skip the header
                            }
                            int degreeTotalCredit = 0; //since we read each file for each degree, we just sum everything up
                            while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                ArrayList<String> lineDetails = new ArrayList<String>();
                                //We begin to look for " and break the line into pieces
                                boolean insideQuotes = false;
                                String lineDetail = "";
                                for(int i=0;i<line.length();i++){
                                    char currentChar = line.charAt(i);
                                    //If char == " then we want to avoid any , until next "
                                    if (currentChar == '\"') {
                                        insideQuotes = !insideQuotes;
                                    }else if (currentChar == ',' && !insideQuotes) {
                                        // Add the current element to the result list
                                        lineDetails.add(lineDetail.trim());
                                        // Reset the StringBuilder for the next element
                                        lineDetail = "";
                                    } else {
                                        // Append the character to the current element
                                        lineDetail+=currentChar;
                                    }
                                    //If this is last loop and lineDetail not empty then we need to add it
                                    if(i+1 == line.length() && lineDetail.length()>=0){
                                        // Add the current element to the result list
                                        lineDetails.add(lineDetail.trim());
                                        // Reset the StringBuilder for the next element
                                        lineDetail = "";
                                    }
                                }
                                //Now we read the csv data and process here:
                                //First column: Degree
                                String degree = lineDetails.get(0);
                                degree = degree.trim();
                                degree = degree.toUpperCase();
                                DegreeEntity degreeEntity = new DegreeEntity(); //create a new degree entity variable
                                if(degreeService.degreeExistByName(degree)) degreeEntity = degreeService.selectDegreeByName(degree); //select if degree already exist
                                degreeEntity.setName(degree);//set name
                                if(degreeEntity.getId()==null) degreeEntity = degreeService.createDegree(degreeEntity); // save/create degree entity in database
                                //Second column: Requirement
                                String degreeReqCat = lineDetails.get(1);
                                degreeReqCat = degreeReqCat.trim();
                                degreeReqCat = degreeReqCat.toUpperCase();
                                //Third column: Minimum credits
                                int degreeReqMinCre = Integer.parseInt(lineDetails.get(2).trim()); //we combine everything to make the code shorter
                                //Check if degree req already exist:
                                Set<DegreeReqEntity> existingDegreeReqs = degreeEntity.getDegreeReqEntities();
                                DegreeReqEntity degreeReqEntity = new DegreeReqEntity(); //create a new degreeRequirementEntity
                                for(DegreeReqEntity existingDegreeReq: existingDegreeReqs){
                                    System.out.println(existingDegreeReq.getName());
                                    if(existingDegreeReq.getName().equals(degreeReqCat)){ //degree name matches
                                        degreeReqEntity = existingDegreeReq; //we assign it to current variable name
                                        break;
                                    }
                                }
                                degreeReqEntity.setName(degreeReqCat); //set name
                                degreeReqEntity.setMinimumCredit(degreeReqMinCre); //set minimum credit
                                degreeReqEntity.setDegreeEntity(degreeEntity); //connect degree req entity to degree
                                degreeEntity.getDegreeReqEntities().add(degreeReqEntity); //connect degree entity to degree req
                                degreeReqService.createDegreeReq(degreeReqEntity); //save degree req into database
                                degreeTotalCredit+=degreeReqMinCre;//sum up every min credit to make up the degree total min credit
                                degreeEntity.setTotalCredit(degreeTotalCredit); //we update it everytime and it will be updated to the final one
                            }
                            scanner.close();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    // You can perform other operations with the InputStream as needed

                } catch (IOException e) {
                    // Handle exceptions, e.g., file not found, etc.
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
                }
            }
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Accepted");
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
        }
    }
}
