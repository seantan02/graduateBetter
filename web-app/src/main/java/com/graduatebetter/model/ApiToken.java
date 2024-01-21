package com.graduatebetter.model;

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
@Table(name = "api_token")
public class ApiToken implements Comparable<ApiToken>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "rate")
    private int rate;

    @Override
    public int compareTo(ApiToken o) {
        if(!(o instanceof ApiToken)) throw new IllegalArgumentException("Only course can be compare to course");
        return this.token.compareTo(o.token);
    }
}
