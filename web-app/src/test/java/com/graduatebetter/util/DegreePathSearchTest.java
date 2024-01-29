package com.graduatebetter.util;

import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DegreePathSearchTest {
    private List<Course> sampleListOfCourse = new ArrayList<Course>();

    @BeforeEach
    public void setUp(){
        Course course1 = new Course();
        course1.setCode("A");
        course1.setCredits(3);
        course1.getMajor().add("Major 1");
        course1.setTitle("Title A");

        Course course2 = new Course();
        course1.setCode("B");
        course1.setCredits(3);
        course1.getMajor().add("Major 1");
        course1.setTitle("Title B");
        this.sampleListOfCourse.add(course1);
        this.sampleListOfCourse.add(course2);
    }

    @Test 
    public void testReset(){
        DegreePathSearch degreePathSearch = new DegreePathSearch();

        for(Course course: this.sampleListOfCourse){
            degreePathSearch.addCourse(course);
        }
        Assertions.assertEquals(2, degreePathSearch.getCodeToCourse().size());
        Assertions.assertEquals(2, degreePathSearch.getCodeToCourse().size());
        degreePathSearch.reset();
        Assertions.assertEquals(0, degreePathSearch.getCodeToCourse().size());
        Assertions.assertEquals(0, degreePathSearch.getCodeToCourse().size());
    }
}
