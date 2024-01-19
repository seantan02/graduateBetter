package com.graduatebetter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import java.util.HashSet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name="degree_req")
public class DegreeReqEntity implements Comparable<DegreeReqEntity>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "minimum_credit")
    private int minimumCredit;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "degree_id", referencedColumnName = "id", nullable = false)
    private DegreeEntity degreeEntity;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "degree_req_course",
        joinColumns = @JoinColumn(name = "degree_req_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<CourseEntity> courseEntities = new HashSet<CourseEntity>();

    @Override
    public int compareTo(DegreeReqEntity d) {
        if(!(d instanceof DegreeReqEntity)) throw new IllegalArgumentException("Only degreeRequirement can be compare to degreeRequirement");
        return this.id.compareTo(d.id)+this.name.compareTo(d.name);
    }
}
