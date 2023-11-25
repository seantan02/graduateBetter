import java.util.ArrayList;

public class Course {
    public String code;
    public int credits;    
    public ArrayList<ArrayList<String>> preRequesits;
    public ArrayList<String> satisfiedCategories;
    public Course(String _code, int _credits, ArrayList<ArrayList<String>> _preRequesits, ArrayList<String> _satsfiedCategories){
        code = _code;
        credits = _credits;
        preRequesits = _preRequesits;
        satisfiedCategories = _satsfiedCategories;
    }
}
