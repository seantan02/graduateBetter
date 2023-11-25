import java.util.HashMap;
public class test{
    public static void main(String[] args){
        HashMap<String, Course> sample = SampleDataSet.generateData();
        CourseGraph c = new CourseGraph();
        c.setCourses(sample);
        HashMap<String,Integer> credsRem = new HashMap<String,Integer>();
        credsRem.put("DS_PROBABILITY",5);
        credsRem.put("CS_ELECTIVES",6);
        credsRem.put("DS_ELECTIVES",5);
        credsRem.put("CS_ALGORITHMS",5);
        credsRem.put("DS_BIGDATA",5);
        credsRem.put("CS_APPLICATIONS",5);
        c.setCredsRemaining(credsRem);
        c.computeCourses();
        for(String s : c.takenCourses){
            System.out.println(s);
        }

        System.out.println("LONGEST PATH IN TAKEN COURSES: ");
        for(String s : c.shortestPath.get(c.lp)){
            System.out.println(s);
        }
    }
}
