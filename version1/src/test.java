import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
public class test{
    public static void main(String[] args){
        Dataset dataset = new Dataset();
        dataset.readFile("version1/prerequisites.csv");
        ArrayList<Course> csCourses = dataset.filterCourseByPrefix("CS", 25);
        ArrayList<Course> dsCourses = dataset.filterCourseByPrefix("STAT", 25);
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
                credsRem.put(degreeRequirement,3);
            }
        }
        courseSearch.setCredReqs(credsRem);
        ArrayList<String> result = courseSearch.findBestCombo(10);
        for(String s : result) System.out.println(s);
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
    }
}
