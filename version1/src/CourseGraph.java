import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


public class CourseGraph{
    private HashMap<String, Integer> visited;
    private int totalCredsRemaining;
    private HashMap<String, Integer> credsRemaining;
    private HashMap<String, Course> courses;
    public List<String> takenCourses;
    public HashMap<String,List<String>> shortestPath;
    public String lp;
    public CourseGraph(){
        totalCredsRemaining = 0;
        this.visited = new HashMap();
        this.credsRemaining = new HashMap();
        this.courses = new HashMap();
        this.takenCourses = new ArrayList<String>();
        this.shortestPath = new HashMap();
    }
    public void setCourses(HashMap<String,Course> c){
        this.courses = c;
    }
    public void setCredsRemaining(HashMap<String,Integer> cr){
        totalCredsRemaining = 0;
        cr.forEach((key,value) -> {
            totalCredsRemaining += value;
        });
        credsRemaining = cr;
    }
    public void addCourse(String s, Coures c){
        courses.put(s,c);
    }
    public void computeCourses(){
        while(totalCredsRemaining > 0){
            String bestCourse = "";
            int bestVal = -10000;
            for(HashMap.Entry<String,Course> entry : courses.entrySet()){
                String key = entry.getKey();
                Course value = entry.getValue();
                if(visited.containsKey(key)) continue;
                int cCredits = value.credits;   
                int val = -cCredits;

                for(String req : value.satisfiedCategories){
                    int cRem = credsRemaining.get(req);
                    val+=Math.max(0,Math.min(cRem,cCredits));
                    if(cRem-cCredits > 0 && cRem-cCredits < 3){
                        val+=(cRem-cCredits)-3;
                    }
                }
                
                if(bestCourse.length() == 0){
                    bestCourse = key;
                    bestVal=val;
                }
                else if(bestVal < val){
                    bestCourse = key;
                    bestVal = val;
                }
            }
            takenCourses.add(bestCourse); 
            Course c = courses.get(bestCourse);
            for(String req : c.satisfiedCategories){
                 int newVal = Math.max(0,credsRemaining.get(req)-c.credits);
                 int usedCredits = credsRemaining.get(req);
                 if(c.credits < credsRemaining.get(req)) usedCredits = c.credits;
                 totalCredsRemaining-=usedCredits;
                 credsRemaining.put(req,newVal);
            }
            visited.put(bestCourse,1);
        }
        lp = takenCourses.get(0);
        for(String s : takenCourses){
            getShortestPath(s);
            if(shortestPath.get(s).size() > shortestPath.get(lp).size()){
                lp = s;
            }
        }
    }
    List<String> getShortestPath(String c){
        if(shortestPath.containsKey(c)) return shortestPath.get(c);
        List<String> res = new ArrayList<String>();
        res.add(c);
        Course curCourse = courses.get(c);
        for(List<String> a : curCourse.preRequesites){
            List<String> best = getShortestPath(a.get(0));
            for(String s : a){
                List<String> cur = getShortestPath(s);
                if(cur.size() < best.size()){
                    best = cur;
                }
            }
            for(String s : best){
                res.add(s);
            }
        }
        shortestPath.put(c,res);
        return res;
    }
}