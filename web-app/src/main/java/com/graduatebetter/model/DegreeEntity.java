package com.graduatebetter.model;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "degree")
public class DegreeEntity implements Comparable<DegreeEntity>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "total_credit")
    private int totalCredit;

    @JsonIgnore
    @OneToMany(mappedBy = "degreeEntity")
    private Set<DegreeReqEntity> degreeReqEntities = new HashSet<DegreeReqEntity>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "degree_course",
        joinColumns = @JoinColumn(name = "degree_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<CourseEntity> courseEntities = new HashSet<CourseEntity>();

    @Override
    public int compareTo(DegreeEntity d) {
        if(!(d instanceof DegreeEntity)) throw new IllegalArgumentException("Only degree can be compare to degree");
        return this.name.compareTo(d.name);
    }
}
