package com.graduatebetter.model;

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
@Table(name="shortest_path_result")
public class ShortestPathResultEntity implements Comparable<ShortestPathResultEntity>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private CourseEntity courseEntity;

    @ManyToOne
    @JoinColumn(name = "shortest_path_id", referencedColumnName = "id", nullable = false)
    private ShortestPathEntity ShortestPathEntity;

    @Override
    public int compareTo(ShortestPathResultEntity spr) {
        if(!(spr instanceof ShortestPathResultEntity)) throw new IllegalArgumentException("Only degreeRequirement can be compare to degreeRequirement");
        return this.id.compareTo(spr.id);
    }
}
