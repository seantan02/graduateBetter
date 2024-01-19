package com.graduatebetter.util;

public class StringUtil {
    public String getUniqueDegreeCatString(String degree, String requirement){
        return degree.replaceAll("\\s", "").toUpperCase()+requirement.toUpperCase();
    }
}
