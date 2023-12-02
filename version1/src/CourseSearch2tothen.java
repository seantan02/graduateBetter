import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


public class CourseSearch2tothen{
    int curCourseNum;
    private HashMap<String, Integer> credReqs;
    private HashMap<String, Integer> courseId;
    private List<Course> courseList;
    public CourseSearch2then(){
        curCourseNum = 0;
        this.creqReqs = new HashMap();
        this.courseId = new HashMap();
        this.courseList = new HashMap();
    }
    public boolean addCourse(Course c){
        if(curCourseNum > 30) return false;
        if(courseId.containsKey(c.code)) return false;
        courseId.put(c,code,curCourseNum);
        courseList.add(c);
        curCourseNum++;
    }
    public void setCredReqs(HashMap<String,Integer> cr){
        this.credReqs = cr;
    }
    public boolean checkPreReqsSatisfied(int mask, int cId){
        Course curCourse = courseList.get(courseId.get(cId));
        for(List<String> reqs : curCourse.preRequisites){
            boolean sat = false;
            for(String req : reqs){
                int reqId = courseId.get(req);
                if((1<<reqId)&mask != 0){
                    sat = true;
                    break;
                }
            }
            if(!sat) return false;
        }
        return true;
    }
    public int checkCourseCombo(int mask){
        HashMap<String, Integer> cr = credReqs.clone();
        int credsTaken = 0;
        for(int i = 0; i < curCourseNum; i++){
            if((1<<i)&mask == 0){
                continue;
            }
            if(!checkPreReqsSatisfied(mask,i)){
                return Integer.MAX_VALUE;
            }
            Course curCourse = courseList.get(courseId.get(cId));
            credsTaken += curCourse.credits;
            for(String s : curCourse.satisfiedCategories){
                cr.put(s,cr.get(s)-curCoures.credits);
            }
        }
        for(HashMap.Entry<String,Integer> req : cr.entrySet()){
            if(req.value > 0) return Integer.MAX_VALUE;
        }
        return credsTaken;
    }
    public List<String> findBestCombo(){
        int bestMask = -1;
        int bestVal = Integer.MAX_VALUE;
        for(int i = 0; i < (1<<curCourseNum); i++){
            int check = checkCourseCombo(i);
            if(check < bestVal){
                bestVal = check;
                bestMask = i;
            }
        }
        List<String> res = new ArrayList<String>();
        for(int i = 0; i < curCourseNum; i++){
            if((1<<i)&bestMask == 0) continue;
            Course curCourse = courseList.get(i);
            res.add(curCourse.code);
        }
        return res;
    }
}

