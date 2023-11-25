import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * This class models reading a csv dataset and creating graph
 */
public class SampleDataSet {
    private static HashMap courseMap = new HashMap<String, Course>();

    public static HashMap generateData(){
        List<Course> courses = new ArrayList<Course>();
        //Add sample courses
        courses.add(new Course("CS-1", 3, null, null));
        //Pre Requesits
        List<List<String>> cs2PreRequesits = new ArrayList<>();
        List<String> cs2PreRequesits1 = new ArrayList<String>();
        cs2PreRequesits1.add("CS-1");
        cs2PreRequesits.add(cs2PreRequesits1);
        //categories
        List<String> cs2Categories = new ArrayList<String>();
        cs2Categories.add("CS_APPLICATIONS");
        courses.add(new Course("CS-2", 3,  cs2PreRequesits, cs2Categories));
        courses.add(new Course("CS-3", 4, null, null));
        courses.add(new Course("CS-4", 3, null, null));
        //Pre Requesits
        List<List<String>> cs5PreRequesits = new ArrayList<>();
        List<String> cs5PreRequesits1 = new ArrayList<String>();
        cs5PreRequesits1.add("CS-3");
        cs5PreRequesits1.add("CS-1");
        List<String> cs5PreRequesits2 = new ArrayList<String>();
        cs5PreRequesits2.add("CS-4");
        cs5PreRequesits.add(cs5PreRequesits1);
        cs5PreRequesits.add(cs5PreRequesits2);
        //categories
        List<String> cs5Categories = Arrays.asList("CS_APPLICATIONS", "CS_ALGORITHMS", "CS_ELECTIVES");
        courses.add(new Course("CS-5", 5, cs5PreRequesits, cs5Categories));
        courses.add(new Course("CS-6", 4, null, null));
        
        List<List<String>> cs7Prerequisites = new ArrayList<>();
        List<String> cs7Prerequisites1 = new ArrayList<>();
        cs7Prerequisites1.add("CS-5");
        cs7Prerequisites.add(cs7Prerequisites1);
        List<String> cs7Categories = new ArrayList<>();
        cs7Categories.add("CS_ELECTIVES");
        cs7Categories.add("DS_ELECTIVES");
        courses.add(new Course("CS-7", 3, cs7Prerequisites, cs7Categories));
        
        courses.add(new Course("CS-8", 4, null, null));
        
        List<List<String>> cs9Prerequisites = new ArrayList<>();
        List<String> cs9Prerequisites1 = new ArrayList<>();
        cs9Prerequisites1.add("CS-6");
        cs9Prerequisites.add(cs9Prerequisites1);
        List<String> cs9Categories = new ArrayList<>();
        cs9Categories.add("CS_ALGORITHMS");
        courses.add(new Course("CS-9", 5, cs9Prerequisites, cs9Categories));

        courses.add(new Course("CS-10", 3, null, null));

        List<List<String>> cs11Prerequisites = new ArrayList<>();
        List<String> cs11Prerequisites1 = new ArrayList<>();
        cs11Prerequisites1.add("CS-9");
        cs11Prerequisites.add(cs11Prerequisites1);
        List<String> cs11Categories = new ArrayList<>();
        cs11Categories.add("CS_ELECTIVES");
        cs11Categories.add("DS_ELECTIVES");
        courses.add(new Course("CS-11", 4, cs11Prerequisites, cs11Categories));

        courses.add(new Course("CS-12", 3, null, null));

        List<List<String>> cs13Prerequisites = new ArrayList<>();
        List<String> cs13Prerequisites1 = new ArrayList<>();
        cs13Prerequisites1.add("CS-8");
        cs13Prerequisites.add(cs13Prerequisites1);
        List<String> cs13Categories = Arrays.asList("CS_APPLICATIONS", "CS_ELECTIVES", "DS_ELECTIVES");
        courses.add(new Course("CS-13", 5, cs13Prerequisites, cs13Categories));

        courses.add(new Course("CS-14", 4, null, null));

        List<List<String>> cs15Prerequisites = new ArrayList<>();
        List<String> cs15Prerequisites1 = new ArrayList<>();
        cs15Prerequisites1.add("CS-11");
        cs15Prerequisites.add(cs15Prerequisites1);
        List<String> cs15Categories = Arrays.asList("CS_ALGORITHMS", "CS_ELECTIVES");

        courses.add(new Course("CS-15", 3, cs15Prerequisites, cs15Categories));
        //DS
        //Pre Requesits
        List<List<String>> ds2PreRequesits = new ArrayList<>();
        List<String> ds2PreRequesits1 = new ArrayList<String>();
        ds2PreRequesits1.add("DS-1");
        ds2PreRequesits.add(cs2PreRequesits1);
        //categories
        List<String> ds2Categories = new ArrayList<String>();
        ds2Categories.add("DS_BIGDATA");
        courses.add(new Course("DS-2", 3,  ds2PreRequesits, ds2Categories));
        courses.add(new Course("DS-3", 4, null, null));
        courses.add(new Course("DS-4", 3, null, null));
        //Pre Requesits
        List<List<String>> ds5PreRequesits = new ArrayList<>();
        List<String> ds5PreRequesits1 = new ArrayList<String>();
        ds5PreRequesits1.add("DS-3");
        ds5PreRequesits1.add("DS-1");
        List<String> ds5PreRequesits2 = new ArrayList<String>();
        ds5PreRequesits2.add("CS-4");
        ds5PreRequesits.add(ds5PreRequesits1);
        ds5PreRequesits.add(ds5PreRequesits2);
        //categories
        List<String> ds5Categories = Arrays.asList("DS_BIGDATA", "DS_PROBABILITY", "DS_ELECTIVES");
        courses.add(new Course("DS-5", 5, ds5PreRequesits, ds5Categories));
        courses.add(new Course("DS-6", 4, null, null));
        // DS-7
        List<List<String>> ds7Prerequisites = new ArrayList<>();
        List<String> ds7Prerequisites1 = new ArrayList<>();
        ds7Prerequisites1.add("DS-5");
        ds7Prerequisites.add(ds7Prerequisites1);
        List<String> ds7Categories = Arrays.asList("DS_PROBABILITY", "CS_ELECTIVES", "DS_ELECTIVES");
        courses.add(new Course("DS-7", 3, ds7Prerequisites, ds7Categories));
        // DS-8
        courses.add(new Course("DS-8", 4, null, null));

        // DS-9
        List<List<String>> ds9Prerequisites = new ArrayList<>();
        List<String> ds9Prerequisites1 = new ArrayList<>();
        ds9Prerequisites1.add("DS-6");
        ds9Prerequisites.add(ds9Prerequisites1);
        List<String> ds9Categories = Arrays.asList("CS_ELECTIVES", "DS_ELECTIVES");
        courses.add(new Course("DS-9", 5, ds9Prerequisites, ds9Categories));

        // DS-10
        courses.add(new Course("DS-10", 3, null, null));

        // DS-11
        List<List<String>> ds11Prerequisites = new ArrayList<>();
        List<String> ds11Prerequisites1 = new ArrayList<>();
        ds11Prerequisites1.add("DS-9");
        ds11Prerequisites.add(ds11Prerequisites1);
        List<String> ds11Categories = Arrays.asList("DS_PROBABILITY", "DS_BIGDATA", "CS_ALGORITHMS");
        courses.add(new Course("DS-11", 4, ds11Prerequisites, ds11Categories));

        // DS-12
        courses.add(new Course("DS-12", 3, null, null));

        // DS-13
        List<List<String>> ds13Prerequisites = new ArrayList<>();
        List<String> ds13Prerequisites1 = new ArrayList<>();
        ds13Prerequisites1.add("DS-8");
        ds13Prerequisites.add(ds13Prerequisites1);
        List<String> ds13Categories = Arrays.asList("DS_BIGDATA", "CS_APPLICATIONS");
        courses.add(new Course("DS-13", 5, ds13Prerequisites, ds13Categories));

        // DS-14
        courses.add(new Course("DS-14", 4, null, null));

        // DS-15
        List<List<String>> ds15Prerequisites = new ArrayList<>();
        List<String> ds15Prerequisites1 = new ArrayList<>();
        ds15Prerequisites1.add("DS-11");
        ds15Prerequisites.add(ds15Prerequisites1);
        List<String> ds15Categories = Arrays.asList("DS_PROBABILITY", "DS_ELECTIVES");
        courses.add(new Course("DS-15", 3, ds15Prerequisites, ds15Categories));
        //CS
        for(int i =0;i<15;i++){
            String[] codes = {"CS-1", "CS-2", "CS-3", "CS-4", "CS-5", "CS-6", "CS-7", "CS-9", "CS-8", "CS-9", "CS-10", "CS-11", "CS-12", "CS-13", "CS-14", "CS-15"};
            courseMap.put(codes[i], courses.get(i));
        }
        //DS
        for(int i =0;i<15;i++){
            String[] codes = {"DS-1", "DS-2", "DS-3", "DS-4", "DS-5", "DS-6", "DS-7", "DS-9", "DS-8", "DS-9", "DS-10", "DS-11", "DS-12", "DS-13", "DS-14", "DS-15"};
            courseMap.put(codes[i], courses.get(i+14));
        }
        return courseMap;
    }

    public static void main(String[] args) {
        HashMap<String, Course> sample = SampleDataSet.generateData();
        System.out.println(sample.get("CS-1").getCode());;
    }
}
