import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class CourseSearch2tothen{
    private int highestCredits;
    private int curCourseNum;
    private HashMap<String, Integer> credReqs;
    private HashMap<String, Integer> courseId;
    private List<Course> courseList;
    public CourseSearch2tothen(){
        this.curCourseNum = 0;
        this.highestCredits = 0;
        this.credReqs = new HashMap<String, Integer>();
        this.courseId = new HashMap<String, Integer>();
        this.courseList = new ArrayList();
    }
    public boolean addCourse(Course c){
        if(curCourseNum >= 50) return false;
        if(this.courseId.containsKey(c.code)) return false;
        this.courseId.put(c.code,curCourseNum);
        courseList.add(c);
        if(c.credits>this.highestCredits){
            this.highestCredits = c.credits;
        }
        curCourseNum++;
        return true;
    }
    public void setCredReqs(HashMap<String,Integer> cr){
        this.credReqs = cr;
    }
    public boolean checkPreReqsSatisfied(int mask, int cId){
        Course curCourse = courseList.get(cId);
        for(List<String> reqs : curCourse.preRequesites){
            boolean sat = false;
            for(String req : reqs){
                int reqId = this.courseId.get(req);
                if(((1<<reqId)&mask) != 0){
                    sat = true;
                    break;
                }
            }
            if(!sat) return false;
        }
        return true;
    }
    public int checkCourseCombo(int mask){
        HashMap<String, Integer> cr = new HashMap<>();
        for(HashMap.Entry<String,Integer> req : credReqs.entrySet()){
            cr.put(req.getKey(),req.getValue());
        }

        int credsTaken = 0;
        for(int i = 0; i < curCourseNum; i++){
            if(((1<<i)&mask) == 0){
                continue;
            }
            if(!checkPreReqsSatisfied(mask,i)){
                return Integer.MAX_VALUE;
            }
            Course curCourse = courseList.get(i);
            credsTaken += curCourse.credits;
            for(String s : curCourse.satisfiedCategories){
                cr.put(s,cr.get(s)-curCourse.credits);
            }
        }
        for(HashMap.Entry<String,Integer> req : cr.entrySet()){
            if(req.getValue() > 0) return Integer.MAX_VALUE;
        }
        return credsTaken;
    }
    public ArrayList<String> findBestCombo(int highestCategoryCredits) {
        int minCourses = (int) (Math.floor((double) highestCategoryCredits / this.highestCredits) + 1);
        int bestMask = -1;
        int bestVal = Integer.MAX_VALUE;
    
        // Iterate through all possible combinations of courses using a bitmask
        for (int i = (1 << minCourses) - 1; i < (1 << curCourseNum); i++) {
            int check = checkCourseCombo(i);
    
            // Update best combination if the current one is better
            if (check < bestVal) {
                bestVal = check;
                bestMask = i;
            }
        }
    
        ArrayList<String> res = new ArrayList<String>();
    
        // Extract the courses from the best combination using the bitmask
        for (int i = 0; i < curCourseNum; i++) {
            if (((1 << i) & bestMask) == 0) continue;
            Course curCourse = courseList.get(i);
            res.add(curCourse.code);
        }
    
        return res;
    }    
}

