import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Dataset {
    private int numberOfCourses;
    private HashSet<String> degree;
    private HashMap<String, List<Course>> degreeToCourse;
    private HashMap<String, HashSet<String>> degreeRequirements;
    private HashMap<String, Integer> requirementToCredit;
    private HashMap<String, Course> codeToCourses;
    private ArrayList<Course> courses;

    public Dataset(){
        this.numberOfCourses = 0;
        this.degree = new HashSet<String>();
        this.degreeToCourse = new HashMap<String, List<Course>>();
        this.degreeRequirements = new HashMap<String, HashSet<String>>();
        this.requirementToCredit = new HashMap<String, Integer>();
        this.codeToCourses = new HashMap<String, Course>();
        this.courses = new ArrayList<Course>();
    }

    //Accessor
    public int size(){
        return this.numberOfCourses;
    }
    
    public HashSet<String> getDegree(){
        return this.degree;
    }
    public HashMap<String, HashSet<String>> getDegreeRequirements(){
        return this.degreeRequirements;
    }

    public void readFile(String filepath){
        //Read file
        try {
            File file = new File(filepath);
            try (Scanner scanner = new Scanner(file)) {
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
                    Course course = new Course("", "", 0, null, new HashSet<String>(), null);
                    //First column: Class code
                    String classCode = lineDetails.get(0);
                    classCode = classCode.trim();
                    classCode = classCode.toUpperCase();
                    if(this.codeToCourses.containsKey(classCode)){
                        course = this.codeToCourses.get(classCode);
                    }else{
                        course.setCode(classCode);
                    }
                    //Second Column: Title
                    String classTitle = lineDetails.get(1);
                    classTitle = classTitle.trim();
                    classTitle = classTitle.toUpperCase();
                    if(!course.title.equals("")) course.setTitle(classTitle); //only update if the course is new (title = "")
                    //Third Column: Credits
                    int credits = Integer.parseInt(lineDetails.get(2));
                    if(course.credits == 0){ //if new course (credit == 0 ) then update
                        course.setCredits(credits);
                    }else{//check if previous course has different credit, if yes, throw exception
                        if(course.credits != credits) throw new IllegalStateException("Course with code: "+course.code+" has a duplicate that has different credit. Previous: "+course.credits+". New: "+credits);
                    }
                    //Fourth Column: Major
                    String major = lineDetails.get(3);
                    major = major.trim();
                    major = major.toUpperCase();
                    this.degree.add(major);//it's a hashset so it wont have duplicate
                    course.major.add(major);//it's a hashset too
                    //Create a new HashSet for this course category if it doesnt exist
                    if(!course.majorSatisfiedCategories.containsKey(major)){
                        HashSet<String> majorSatisfiedCat = new HashSet<String>();
                        course.majorSatisfiedCategories.put(major, majorSatisfiedCat);
                    }
                    //Now we add the course to the hashmap for degree
                    if(this.degreeToCourse.containsKey(major)){//If there's a list already in the hashmap
                        List<Course> currentDegreeCourseList = degreeToCourse.get(major);
                        currentDegreeCourseList.add(course);
                        degreeToCourse.replace(major, currentDegreeCourseList);
                    }else{
                        List<Course> newDegreeCourseList = new ArrayList<Course>();
                        newDegreeCourseList.add(course);
                        degreeToCourse.put(major, newDegreeCourseList);
                        //Now we add to course degreeRequirements
                        if(!degreeRequirements.containsKey(major)) degreeRequirements.put(major, new HashSet<String>());
                    }
                    //Fifth Column:  satisfied category
                    if(lineDetails.size() >= 4 && !lineDetails.get(4).equals("")){ //Only perform if category is not empty
                        String[] satisfiedCatList = lineDetails.get(4).split("&");
                        for(String satisfiedCatElem: satisfiedCatList){
                            satisfiedCatElem = satisfiedCatElem.trim();
                            satisfiedCatElem = satisfiedCatElem.toUpperCase();
                            degreeRequirements.get(major).add(satisfiedCatElem);
                            course.majorSatisfiedCategories.get(major).add(satisfiedCatElem);
                            //Initialize the degreeRequirement to 0
                            if(!this.requirementToCredit.containsKey(satisfiedCatElem)) this.requirementToCredit.put(satisfiedCatElem, 0);
                        }
                    }
                    
                    //Sixth column: Pre Requisites
                    if(lineDetails.size() >= 5 && !lineDetails.get(5).equals("")){
                        String[] splitByAnd = lineDetails.get(5).split("&");
                        List<List<String>> preRequisites = new ArrayList<List<String>>();
                        for(String splitByAndPart: splitByAnd){
                            String[] requisites = splitByAndPart.split(",");
                            List<String> listOfRequisite = new ArrayList<String>();
                            for(String requisite: requisites){
                                requisite = requisite.replaceAll("[{}]", "");
                                requisite = requisite.trim();
                                requisite = requisite.toUpperCase();
                                if(requisite.equals(course.code)) throw new IllegalStateException("Requisite cannot be the course itself. Course: "+course.code+" has a requisite of itself.");   //if requisite is the same name as course then throw exception
                                listOfRequisite.add(requisite);
                            }
                            preRequisites.add(listOfRequisite);
                        }
                        course.setpreRequesits(preRequisites);
                    }
                    
                    //END
                    this.courses.add(course);
                    this.codeToCourses.put(course.code, course);
                    this.numberOfCourses += 1;
                }
                scanner.close();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            // Handle file not found exception
            e.printStackTrace();
        }
    }
    
    public void setRequirementCredit(String requirement, int credit){
        String capRequirement = requirement.toUpperCase();
        if(this.degreeRequirements.get(capRequirement) == null) throw new IllegalStateException("Requirement given: "+requirement+" is not a valid requirement.");
        this.degreeRequirements.replace(capRequirement, credit);
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

    public int numberOfDegreeRequirement(String degree){
        String capDegree = degree.toUpperCase();
        if(this.degreeRequirements.get(capDegree)==null) throw new NullPointerException("No requirements are found with degree: "+degree);
        return this.degreeRequirements.get(capDegree).size();
    }

    /**
     * This method is to randomly pick a stirng in a array list
     * @param array
     * @return
     */
    private static String selectRandomString(String[] array) {
        if (array == null || array.length == 0) {
            // Handle the case where the array is empty or null
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(array.length);

        return array[randomIndex];
    }
    /**
     * This method is to randomly pick a stirng in a array list
     * @param array
     * @return
     */
    private static int selectRandomInteger(int[] array) {
        if (array == null || array.length == 0) {
            // Handle the case where the array is empty or null
            return 0;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(array.length);

        return array[randomIndex];
    }

  
}
