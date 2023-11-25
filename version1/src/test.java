import java.util.HashMap;
public class test{
    public static void main(String[] args){
        SampleDataSet sample = new SampleDataSet();
        HashMap hm = sample.generateData();
        CourseGraph c = new CourseGraph();
        c.setCourses(hm);
        HashMap credsRem = new HashMap<String,Integer>();
        credsRem.put("DS_PROBABILITY",5);
        credsRem.put("CS_ELECTIVES",5);
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
