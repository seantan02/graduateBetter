import java.util.List;

public class Course implements Comparable<Course>{
    public String code;
    public int credits;    
    public List<List<String>> preRequesites;
    public List<String> satisfiedCategories;
    public Course(String _code, int _credits, List<List<String>> _preRequesites, List<String> _satsfiedCategories){
        code = _code;
        credits = _credits;
        preRequesites = _preRequesites;
        satisfiedCategories = _satsfiedCategories;
    }
    //accessor
    public String getCode(){
        return this.code;
    }
    //mutator
    public void setCode(String code){
        this.code = code;
    }
    public void setpreRequesits(List<List<String>> preRequesits){
        this.preRequesits = preRequesits;
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
}
