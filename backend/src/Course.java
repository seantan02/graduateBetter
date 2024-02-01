import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashSet;
import java.util.HashMap;

public class Course implements Comparable<Course>{
    public String code;
    public String title;
    public int credits;    
    public List<List<String>> preRequesites;
    public HashSet<String> major;
    public HashMap<String, HashSet<String>> majorSatisfiedCategories;
    public Course(String _code, String _title, int _credits, List<List<String>> _preRequesites, HashSet<String> _major, HashMap<String, HashSet<String>> _majorSatisfiedCategories){
        code = _code;
        title = _title;
        credits = _credits;
        if(_preRequesites == null){
            this.preRequesites = new ArrayList<>();
        }else{
            this.preRequesites = _preRequesites;
        }
        this.major = _major;
        if(_majorSatisfiedCategories == null){
            this.majorSatisfiedCategories = new HashMap<String, HashSet<String>>();
        }else{
            this.majorSatisfiedCategories = _majorSatisfiedCategories;
        }
        
    }
    
    //accessor
    public String getCode(){
        return this.code;
    }
    //mutator
    public void setCode(String code){
        this.code = code;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setpreRequesits(List<List<String>> preRequesites){
        this.preRequesites = preRequesites;
    }
    public void setMajor(String major){
        this.major = major;
    }
    public void setCredits(int credits){
        this.credits = credits;
    }
    public void setSatisfiedCategories(HashMap<String, HashSet<String>> _majorSatisfiedCategories){
        this.majorSatisfiedCategories = _majorSatisfiedCategories;
    }

    public int compareTo(Course c){
        return this.credits-c.credits;//fixed
    }

    public String toString(){
        String res = "Code: " + code + ", Title: " + this.title + ", Credits: " + credits + " Categories: ";
        for(HashMap.Entry<String, HashSet<String>> entry: majorSatisfiedCategories.entrySet()){
            for(String s : entry.getValue()) res += entry.getKey()+": "+s + ", ";
            res += "Pre Reqs: ";
        }
        

        for(List<String> reqs : this.preRequesites){
            res+="{";
            for(String req : reqs){
                res+= req +", ";
            }
            res+="},";
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course toCompare = (Course) o;
        return this.code.equals(toCompare.code);
    }
}
