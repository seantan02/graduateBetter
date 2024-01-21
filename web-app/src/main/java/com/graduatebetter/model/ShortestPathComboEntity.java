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
@Table(name="shortest_path_combo")
public class ShortestPathComboEntity implements Comparable<ShortestPathComboEntity>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "degree_id", referencedColumnName = "id", nullable = false)
    private DegreeEntity degreeEntity;

    @ManyToOne
    @JoinColumn(name = "shortest_path_id", referencedColumnName = "id", nullable = false)
    private ShortestPathEntity shortestPathEntity;

    @Override
    public int compareTo(ShortestPathComboEntity spc) {
        if(!(spc instanceof ShortestPathComboEntity)) throw new IllegalArgumentException("Only degreeRequirement can be compare to degreeRequirement");
        return this.id.compareTo(spc.id);
    }
}
