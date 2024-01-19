package com.graduatebetter.service;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

public class MainBanner implements Banner {

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, java.io.PrintStream out) {
        out.println("Custom Banner");
        out.println("--------------");
        // Your banner content goes here
        out.println("--------------");
    }
}
