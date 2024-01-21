package com.graduatebetter.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DegreePathRequest {
    private List<String> degreeIds;
}
