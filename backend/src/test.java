import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
public class test{
    private static void computeBestCousePathWithASearch(){
        Dataset dataset = new Dataset();
        dataset.readFile("version1/prerequisites.csv");
        ArrayList<Course> csCourses = dataset.filterCourseByPrefix("CS",10);
        ArrayList<Course> dsCourses = dataset.filterCourseByPrefix("STAT", 10);

        int numberOfDegreeRequirements = dataset.numberOfDegreeRequirement("CS");
        numberOfDegreeRequirements += dataset.numberOfDegreeRequirement("DS");

        CoursePathSearch courseSearch = new CoursePathSearch(numberOfDegreeRequirements);
        
        for(Course csCourse : csCourses){
            courseSearch.addCourse(csCourse);
            System.out.println(csCourse);
        } 
        for(Course dsCourse : dsCourses){
            courseSearch.addCourse(dsCourse);
            System.out.println(dsCourse);
        } 

        HashMap<String, Integer> categoryToIndex = courseSearch.getCategoryHashTable();
        int[] categoriesValues = new int[categoryToIndex.size()];
        for(String degree:dataset.getDegree()){
            for(String degreeRequirement: dataset.getDegreeRequirements().get(degree)){
                Random random = new Random();
                int randomNumber = random.nextInt(8, 10);
                int index = -1;
                try{
                    index = categoryToIndex.get(degreeRequirement);
                }catch(Exception e){
                }
                //If no category found
                if(index == -1){
                    System.out.println("No classes have satisfy "+degreeRequirement+". Impossible.");
                    break;
                }
                categoriesValues[index] = randomNumber;
            }
        }
        
        System.out.println("Start state:");
        for(int categoriesValue : categoriesValues){
            System.out.print(categoriesValue+" ");
        }
        courseSearch.setStartGoal(categoriesValues);
        int[] targetState = new int[categoriesValues.length];
        courseSearch.setTargetGoal(targetState);
        System.out.println("\n");

        List<List<int[]>> bestCreComPath = courseSearch.computeShortestPath();
        for(List<int[]> creComPath: bestCreComPath){
            for(int[] creCom: creComPath){
                System.out.println(Arrays.toString(creCom));
            }
            System.out.println();
        }
    }
    public static void main(String[] args){
        Dataset dataset = new Dataset();
        dataset.readFile("version1/prerequisites.csv");
        HashSet<Course> csCourses = dataset.filterCourseByPrefix("CS", 10);
        HashSet<Course> dsCourses = dataset.filterCourseByPrefix("STAT", 10);
        CourseSearch2tothen courseSearch = new CourseSearch2tothen();
        for(Course csCourse : csCourses){
            courseSearch.addCourse(csCourse);
            System.out.println(csCourse);
        } 
        for(Course dsCourse : dsCourses){
            courseSearch.addCourse(dsCourse);
            System.out.println(dsCourse);
        } 

        HashMap<String,Integer> credsRem = new HashMap<String,Integer>();
        for(String degree:dataset.getDegree()){
            for(String degreeRequirement: dataset.getDegreeRequirements().get(degree)){
                credsRem.put(degreeRequirement,6);
            }
        }
        courseSearch.setCredReqs(credsRem);
        ArrayList<String> result = courseSearch.findBestCombo();
        for(String s : result) System.out.println(s);
        // A* Search
        int numberOfDegreeRequirements = dataset.numberOfDegreeRequirement("CS");
        numberOfDegreeRequirements += dataset.numberOfDegreeRequirement("DS");

        CoursePathSearch courseSearch2 = new CoursePathSearch(numberOfDegreeRequirements);
        
        for(Course csCourse : csCourses){
            courseSearch2.addCourse(csCourse);
            System.out.println(csCourse);
        } 
        for(Course dsCourse : dsCourses){
            courseSearch2.addCourse(dsCourse);
            System.out.println(dsCourse);
        } 

        HashMap<String, Integer> categoryToIndex = courseSearch2.getCategoryHashTable();
        int[] categoriesValues = new int[categoryToIndex.size()];
        for(String degree:dataset.getDegree()){
            for(String degreeRequirement: dataset.getDegreeRequirements().get(degree)){
                // Random random = new Random();
                // int randomNumber = random.nextInt(8, 10);
                int randomNumber = 6;
                int index = -1;
                try{
                    index = categoryToIndex.get(degreeRequirement);
                }catch(Exception e){
                }
                //If no category found
                if(index == -1){
                    System.out.println("No classes have satisfy "+degreeRequirement+". Impossible.");
                    break;
                }
                categoriesValues[index] = randomNumber;
            }
        }
        
        System.out.println("Start state:");
        for(int categoriesValue : categoriesValues){
            System.out.print(categoriesValue+" ");
        }
        courseSearch2.setStartGoal(categoriesValues);
        int[] targetState = new int[categoriesValues.length];
        courseSearch2.setTargetGoal(targetState);
        System.out.println("\n");

        List<List<int[]>> allCreComPath = courseSearch2.computeShortestPath(); //This computes the big paths
        List<List<int[]>> bestCreComPath = new ArrayList<List<int[]>>(); //The best path we computed
        List<int[]> bestCreComList = new ArrayList<int[]>(); //The best moves to take in String
        int i = allCreComPath.size()-1; //Set i as the final (When we reach end goal)
        while(i>=0){// when i less than 0 means we get to the start state so stop
            List<int[]> creComPath = allCreComPath.get(i); 
            int parentIndex = creComPath.get(1)[0]; //Get the parent index and trace it back
            bestCreComPath.add(creComPath);
            bestCreComList.add(creComPath.get(2));
            i=parentIndex;
        }
        for(int[] state: bestCreComList){
            System.out.println("Next move state: "+Arrays.toString(state));
        }
        List<Course> bestCourseToTake = courseSearch2.computeBestCourseFromPath(bestCreComList);
        for(Course bestCourse: bestCourseToTake){
            System.out.println(bestCourse.code);
        }

        // HashMap<String, Course> sample = SampleDataSet.generateData();
        // CourseGraph c = new CourseGraph();
        // c.setCourses(sample);
        // HashMap<String,Integer> credsRem = new HashMap<String,Integer>();
        // credsRem.put("DS_PROBABILITY",5);
        // credsRem.put("CS_ELECTIVES",6);
        // credsRem.put("DS_ELECTIVES",5);
        // credsRem.put("CS_ALGORITHMS",5);
        // credsRem.put("DS_BIGDATA",5);
        // credsRem.put("CS_APPLICATIONS",5);
        // c.setCredsRemaining(credsRem);
        // c.computeCourses();
        // for(String s : c.takenCourses){
        //     System.out.println(s);
        // }

        // System.out.println("LONGEST PATH IN TAKEN COURSES: ");
        // for(String s : c.shortestPath.get(c.lp)){
        //     System.out.println(s);
        // }

        /*Using Modified A* Search Algo */
        // computeBestCousePathWithASearch();
    }
}
