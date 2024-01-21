package com.graduatebetter.controller;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController{
    @RequestMapping("/error")
    public String handleError() {
        // Provide your custom error message or redirect to a specific error page
        return "error";
    }

    public String getErrorPath() {
        return "/error";
    }
}
