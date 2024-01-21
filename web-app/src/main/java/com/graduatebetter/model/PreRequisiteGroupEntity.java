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
@Table(name="pre_requisite_group")
public class PreRequisiteGroupEntity implements Comparable<PreRequisiteGroupEntity>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id") Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity courseEntity;

    @JsonIgnore
    @OneToMany(mappedBy = "preRequisiteGroupEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PreRequisiteEntity> preRequisiteEntities = new HashSet<PreRequisiteEntity>();

    @Override
    public int compareTo(PreRequisiteGroupEntity pReq) {
        if(!(pReq instanceof PreRequisiteGroupEntity)) throw new IllegalArgumentException("Only degreeRequirement can be compare to degreeRequirement");
        return this.id.compareTo(pReq.id);
    }
}
