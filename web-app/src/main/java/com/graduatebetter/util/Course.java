package com.graduatebetter.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;

@Getter
@ToString
public class Course implements Comparable<Course>{
    public String code;
    public String title;
    public int credits;    
    public List<List<String>> preRequesites;
    public Set<String> major;
    public HashMap<String, Set<String>> majorSatisfiedCategories;
    private Set<String> disallowedCourses;

    public Course(String _code, String _title, int _credits, List<List<String>> _preRequesites, Set<String> _major, HashMap<String, Set<String>> _majorSatisfiedCategories, Set<String> _disallowedCourses){
        code = _code;
        title = _title;
        credits = _credits;

        if(_preRequesites == null){
            this.preRequesites = new ArrayList<>();
        }else{
            this.preRequesites = _preRequesites;
        }

        if(_major == null){
            this.major = new HashSet<String>();
        }else{
            this.major = _major;
        }

        if(_majorSatisfiedCategories == null){
            this.majorSatisfiedCategories = new HashMap<String, Set<String>>();
        }else{
            this.majorSatisfiedCategories = _majorSatisfiedCategories;
        }

        if(_disallowedCourses == null){
            this.disallowedCourses = new HashSet<String>();
        }else{
            this.disallowedCourses = _disallowedCourses;
        }
    }

    public Course(){
        this("", "", 0, new ArrayList<List<String>>(), new HashSet<String>(), new HashMap<String, Set<String>>(), new HashSet<String>());
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
    public void setMajor(Set<String> major){
        this.major = major;
    }
    public void setCredits(int credits){
        this.credits = credits;
    }
    public void setSatisfiedCategories(HashMap<String, Set<String>> _majorSatisfiedCategories){
        this.majorSatisfiedCategories = _majorSatisfiedCategories;
    }
    public void setDisallowedCourses(Set<String> _disallowedCourses){
        this.disallowedCourses = _disallowedCourses;
    }

    public int compareTo(Course c){
        if(!(c instanceof Course)) throw new IllegalArgumentException("Only course can be compare to course");
        return this.code.compareTo(c.code);
    }
}
