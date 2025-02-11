package com.ksy.fmrs.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int division;

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;

    @Builder
    public League(String name, int division) {
        this.name = name;
        this.division = division;
    }
}
