package com.graduatebetter.webapp.util;

import java.util.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.graduatebetter.util.Course;
import com.graduatebetter.util.DegreePathSearch;

public class DegreePathSearchTest {

    private List<Course> sampleListOfCourse;

    public DegreePathSearchTest(){
        Course course1 = new Course();
        course1.setCode("A");
        course1.setCredits(3);
        course1.getMajor().add("Major 1");
        course1.setTitle("Title A");
        Course course2 = new Course();
        course2.setCode("B");
        course2.setCredits(2);
        course2.getMajor().add("Major 2");
        course2.setTitle("Title B");
        Course course3 = new Course();
        course3.setCode("C");
        course3.setCredits(1);
        course3.getMajor().add("Major 3");
        course3.setTitle("Title C");

        this.sampleListOfCourse.add(course1);
        this.sampleListOfCourse.add(course2);
        this.sampleListOfCourse.add(course3);
    }

    @Test
    public void testReset(){
        DegreePathSearch degreePathSearch = new DegreePathSearch();

        for(Course course: this.sampleListOfCourse){
            degreePathSearch.addCourse(course);
        }
        Assertions.assertEquals(3, degreePathSearch.getCourseList().size());
        Assertions.assertEquals(3, degreePathSearch.getCodeToCourse().size());
        degreePathSearch.reset();
        Assertions.assertEquals(0, degreePathSearch.getCourseList().size());
        Assertions.assertEquals(0, degreePathSearch.getCodeToCourse().size());
    }
    
}
