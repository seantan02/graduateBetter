package com.graduatebetter.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduatebetter.model.CourseEntity;
import com.graduatebetter.service.CourseService;

@RestController
@RequestMapping("/api/v1/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/getAll")
    public List<CourseEntity> getCourses() {
        return courseService.getCourse();
    }
    //Post URL for create
    @PostMapping("/create")
    public ResponseEntity<CourseEntity> createCourse(@RequestBody CourseEntity _course) {
        if(createCourseRequestIsValid(_course)){
            CourseEntity savedCourse = courseService.createCourse(_course);
            System.out.println("New course inserted into database");
            return new ResponseEntity<CourseEntity>(savedCourse, HttpStatus.CREATED);
        }else{
            System.err.println("Course not added to database");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    //Private helper method to check if the request body is valid
    private boolean createCourseRequestIsValid(CourseEntity _course){
        if(_course.getCode().isEmpty()) return false;
        if(_course.getTitle().isEmpty()) return false;
        if(_course.getCredit()<=0) return false;
        return true;
    }

    //DELETE URL for deleting
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteResource(@PathVariable Long id) {
        // Check authorization (You can replace this with your own logic)
        if (!isUserAuthorized()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
        }

        try {
            // Call the service method to delete the resource by ID
            if(courseService.deleteCourse(id)) return ResponseEntity.ok("Resource deleted successfully");
            // Respond with a success message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
        } catch (ResourceNotFoundException e) {
            // Handle the case where the resource with the given ID is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting resource");
        }
    }
    // Replace this method with your actual authorization logic
    private boolean isUserAuthorized() {
        // Your authorization logic here
        // For example, check if the user has the required role or permission
        return true; // Replace with your logic
    }
}