package com.graduatebetter.model;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name="shortest_path")
public class ShortestPathEntity implements Comparable<ShortestPathEntity>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "datetime")
    private ZonedDateTime datetime;

    @Override
    public int compareTo(ShortestPathEntity sp) {
        if(!(sp instanceof ShortestPathEntity)) throw new IllegalArgumentException("Only degreeRequirement can be compare to degreeRequirement");
        return this.id.compareTo(sp.id);
    }
}
