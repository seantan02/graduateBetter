import java.util.ArrayList;
import java.util.List;

public class Course implements Comparable<Course>{
    public String code;
    public int credits;    
    public List<List<String>> preRequesites;
    public List<String> satisfiedCategories;
    public Course(String _code, int _credits, List<List<String>> _preRequesites, List<String> _satsfiedCategories){
        code = _code;
        credits = _credits;
        if(_preRequesites == null){
            this.preRequesites = new ArrayList<>();
        }else{
            this.preRequesites = _preRequesites;
        }
        
        if(_satsfiedCategories == null){
            this.satisfiedCategories = new ArrayList<>();
        }else{
            this.satisfiedCategories = _satsfiedCategories;
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
    public void setpreRequesits(List<List<String>> preRequesites){
        this.preRequesites = preRequesites;
    }
    public void setCredits(int credits){
        this.credits = credits;
    }
    public void setSatisfiedCategories(List<String> satisfiedCategories){
        this.satisfiedCategories = satisfiedCategories;
    }

    public int compareTo(Course c){
        return c.credits-this.credits;
    }
    public String toString(){
        String res = "Code: " + code + ", Credits: " + credits + " Categories: ";
        for(String s : satisfiedCategories) res += s + ", ";
        res += "\n Pre Reqs: ";

        for(List<String> reqs : this.preRequesites){
            res+="{";
            for(String req : reqs){
                res+= req +", ";
            }
            res+="},";
        }
        return res;
    }
}
