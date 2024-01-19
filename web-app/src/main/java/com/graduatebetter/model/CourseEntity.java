package com.graduatebetter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "course")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Add this annotation
public class CourseEntity implements Comparable<CourseEntity>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "title")
    private String title;

    @Column(name = "credit")
    private int credit = 0;

    @JsonIgnore
    @ManyToMany(mappedBy = "courseEntities")
    private Set<DegreeEntity> degreeEntity = new HashSet<DegreeEntity>();

    @JsonIgnore
    @ManyToMany(mappedBy = "courseEntities")
    private Set<DegreeReqEntity> degreeReqEntity = new HashSet<DegreeReqEntity>();

    @JsonIgnore
    @OneToMany(mappedBy = "courseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PreRequisiteGroupEntity> preRequisiteGroupEntity = new HashSet<PreRequisiteGroupEntity>();

    @JsonIgnore
    @OneToMany(mappedBy = "coursePreRequisiteEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreRequisiteEntity> preRequisiteEntity = new ArrayList<PreRequisiteEntity>();

    @Override
    public int compareTo(CourseEntity o) {
        if(!(o instanceof CourseEntity)) throw new IllegalArgumentException("Only course can be compare to course");
        return this.code.compareTo(o.code);
    }
}
