import java.util.ArrayList;

public class Course implements Comparable<Course>{
    public String code;
    public int credits;    
    public ArrayList<ArrayList<String>> preRequesites;
    public ArrayList<String> satisfiedCategories;
    public Course(String _code, int _credits, ArrayList<ArrayList<String>> _preRequesites, ArrayList<String> _satsfiedCategories){
        code = _code;
        credits = _credits;
        preRequesites = _preRequesites;
        satisfiedCategories = _satsfiedCategories;
    }
    public int compareTo(Course c){
        return c.credits-this.credits;
    }
}
