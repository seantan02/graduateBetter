package com.graduatebetter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name="pre_requisite")
public class PreRequisiteEntity implements Comparable<PreRequisiteEntity>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity coursePreRequisiteEntity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pre_requisite_group_id", nullable = false)
    private PreRequisiteGroupEntity preRequisiteGroupEntity;

    @Override
    public int compareTo(PreRequisiteEntity pReq) {
        if(!(pReq instanceof PreRequisiteEntity)) throw new IllegalArgumentException("Only degreeRequirement can be compare to degreeRequirement");
        return this.id.compareTo(pReq.id);
    }
}
