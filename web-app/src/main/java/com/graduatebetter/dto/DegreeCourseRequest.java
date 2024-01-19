package com.graduatebetter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DegreeCourseRequest {
    private String degree;
    private int limit;
}
