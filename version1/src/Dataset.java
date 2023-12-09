import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Dataset {
    private int numberOfCourses;
    private ArrayList<String> degree;
    private HashMap<String, ArrayList<String>> degreeRequirements;
    private ArrayList<Course> courses;

    public Dataset(){
        this.numberOfCourses = 0;
        this.degree = new ArrayList<String>();
        this.degreeRequirements = new HashMap<String, ArrayList<String>>();
        this.courses = new ArrayList<Course>();
    }

    //Accessor
    public ArrayList<String> getDegree(){
        return this.degree;
    }
    public HashMap<String, ArrayList<String>> getDegreeRequirements(){
        return this.degreeRequirements;
    }

    public void readFile(String filepath){
        //Read file
        try {
            File file = new File(filepath);
            Scanner scanner = new Scanner(file);

            // Read each line until the end of the file
            int counter = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                ArrayList<String> lineDetails = new ArrayList<String>();
                if(counter > 0){//Identify header
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
                    }
                }
                //Asumming we have index 0 as class course
                //index 1 is the number of requisite
                //index > 1 is all the requisite
                int credit = selectRandomInteger(new int[] {3,4,5});
                Course course = new Course("", credit, null, null);
                List<String> prerequisite = new ArrayList<String>();
                for(int i=0;i<lineDetails.size();i++){
                    if(i == 0){
                        String classCode = lineDetails.get(i);
                        course.setCode(classCode);

                        ArrayList<String> satisfiedCategories = new ArrayList<String>();
                        String csRandomCategory = "";
                        String dsRandomCategory = "";
                        int oneOrBoth = selectRandomInteger(new int[] {1,2});
                        //CS and DS
                        String[] csCategories = {"Algorithm", "Applications", "CS-Electives"};
                        String[] dsCategories = {"Big-Data", "Machine Learning", "DS-Electives"};
                        csRandomCategory = selectRandomString(csCategories);
                        dsRandomCategory = selectRandomString(dsCategories);
                        //
                        if(oneOrBoth == 1 && classCode.startsWith("CS")){
                            satisfiedCategories.add(csRandomCategory);
                        }else if(oneOrBoth == 1 && classCode.startsWith("STAT")){
                            satisfiedCategories.add(dsRandomCategory);
                        }else{
                            satisfiedCategories.add(csRandomCategory);
                            satisfiedCategories.add(dsRandomCategory);
                        }
                        
                        course.setSatisfiedCategories(satisfiedCategories);
                    }else if(i >1){
                        if(lineDetails.get(i).length()>0)prerequisite.add(lineDetails.get(i));
                    }
                }
                
                List<List<String>> preRequisites = new ArrayList<List<String>>();
                preRequisites.add(prerequisite);
                course.setpreRequesits(preRequisites);
                this.courses.add(course);
                numberOfCourses += 1;
                counter +=1;
            }
            scanner.close();
            //THIS IS FAKE / MADE UP
            this.degree.add("CS");
            this.degree.add("DS");
            ArrayList<String> csDegreeRequirements = new ArrayList<String>();
            csDegreeRequirements.add("Algorithm");
            csDegreeRequirements.add("Applications");
            csDegreeRequirements.add("CS-Electives");
            this.degreeRequirements.put("CS",csDegreeRequirements);
            
            ArrayList<String> dsDegreeRequirements = new ArrayList<String>();
            dsDegreeRequirements.add("Big-Data");
            dsDegreeRequirements.add("Machine Learning");
            dsDegreeRequirements.add("DS-Electives");
            this.degreeRequirements.put("DS", dsDegreeRequirements);

        } catch (FileNotFoundException e) {
            // Handle file not found exception
            e.printStackTrace();
        }
    }
    
    public ArrayList<Course> filterCourseByPrefix(String prefix, int limit){
        ArrayList<Course> filteredCourse = new ArrayList<Course>();
        for(Course course:this.courses){
            if(course.code.startsWith(prefix)){
                filteredCourse.add(course);
                if(limit > 0){
                        limit--;
                        if(limit ==0) break;
                    }
            }
            
        }
        return filteredCourse;
    }

    public int numberOfDegreeRequirement(String degree){
        return this.degreeRequirements.get(degree).size();
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
