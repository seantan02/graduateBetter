package com.graduatebetter.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduatebetter.dto.DegreeCourseRequest;
import com.graduatebetter.service.CourseService;
import com.graduatebetter.service.DegreeReqService;
import com.graduatebetter.service.DegreeService;
import com.graduatebetter.service.PreRequisiteGroupService;
import com.graduatebetter.service.PreRequisiteService;
import com.graduatebetter.util.Course;
import com.graduatebetter.util.DegreeData;

@RestController
@RequestMapping("/api/v1/dataset")
public class DatasetController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private DegreeService degreeService;
    
    @Autowired
    private DegreeReqService degreeReqService;

    @Autowired
    private PreRequisiteService preRequisiteService;

    @Autowired
    private PreRequisiteGroupService preRequisiteGroupService;

    private DegreeData degreeData = null;

    @PostMapping("/getCourse")
    public List<Course> getCourse() {
        if(this.degreeData == null){
            this.degreeData = new DegreeData(this.courseService, this.degreeService, this.degreeReqService, this.preRequisiteService);
        }
        return this.degreeData.getCourses();
    }

    @PostMapping("/getDegreeCourse")
    public List<Course> getDegreeCourse(@RequestBody DegreeCourseRequest request) {
        if(this.degreeData == null){
            this.degreeData = new DegreeData(this.courseService, this.degreeService, this.degreeReqService, this.preRequisiteService);
        }

        this.degreeData.loadDegreeCourse(request.getDegree());
        return new ArrayList<Course>(this.degreeData.filterCourseByDegree(request.getDegree(), request.getLimit()));
    }

    @PostMapping("/getDegreeCourseSize")
    public int getDegreeCourseSize(@RequestBody DegreeCourseRequest request) {
        if(this.degreeData == null){
            this.degreeData = new DegreeData(this.courseService, this.degreeService, this.degreeReqService, this.preRequisiteService);
        }
        
        this.degreeData.loadDegreeCourse(request.getDegree());
        return degreeData.filterCourseByDegree(request.getDegree(), request.getLimit()).size();
    }
}