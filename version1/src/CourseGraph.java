import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue;


public class CourseGraph{
    private HashMap<String, Integer> visited;
    private int totalCredsRemaining;
    private HashMap<String, Integer> credsRemaining;
    private HashMap<String, Course> courses;
    public ArrayList<String> takenCourses;
    public HashMap<String,ArrayList<String>> shortestPath;
    public String lp;
    public CourseGraph(){
        totalCredsRemaining = 0;
        this.visited = new HashMap<String,Integer>();
        this.credsRemaining = new HashMap<String,Integer>();
        this.courses = new HashMap<String,Course>();
        this.takenCourses = new ArrayList<String>();
        this.shortestPath = new HashMaps<String,ArrayList<String>>();
    }
    public addCourse(String s, Coures c){
        courses.put(s,c);
    }
    public void computeCourses(){
        while(totalCredsRemaining > 0){
            String bestCourse = "";
            int bestVal = 0;
            for(Map.Entry<String, Course> it : courses.entrySet){
                if(visited.contains(it.getKey())) continue;
                int cCredits = it.getValue().credits;   
                int val = -cCredits;
                for(String req : it.getValue().satisfiedCategories){
                    int cRem = credsRemaining.get(req);
                    val+=Math.max(0,Math.min(cRem,cCredits));
                    if(cRem-cCredits > 0 && cRem-cCredits < 3){
                        val+=(cRem-cCredits)-3;
                    }
                }
                if(bestCourse.length() == 0)
                    bestCourse = it.getKey();
                else if(bestVal < val){
                    bestCourse = it.getKey();
                    bestVal = val;
                }
            }
            takenCourses.add(bestCourse); 
            Course c = courses.get(bestCourse);
            for(String req : c.satisfiedCategories){
                 int newVal = Math.max(0,credsRemaining.get(req)-c.credits);
                 int usedCredits = credsRemaining.get(req);
                 if(c.credits < credsRemainig.get(req)) usedCredits = c.credits;
                 totalCredsRemaining-=usedCredits;
                 credsRemaining.put(req,newVal);
            }
            visited.put(c,1);
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
        Course cur = courses.get(c);
        for(ArrayList<String> a : cur.preRequisites){
            List<String> best = getShortestPath(a.get(0));
            for(String s : a){
                List<String> cur = getShortestPath(s);
                if(cur.size() < best.size()){
                    best = cur;
                }
            }
            for(string s : best){
                res.add(s);
            }
        }
        shortestPath.put(c,res);
        return res;
    }
}
