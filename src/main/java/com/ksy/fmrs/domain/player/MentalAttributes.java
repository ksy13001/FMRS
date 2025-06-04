package com.ksy.fmrs.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class MentalAttributes {
    private int aggression;
    private int anticipation;
    private int bravery;
    private int composure;
    private int concentration;
    private int decisions;
    private int determination;
    private int flair;
    private int leadership;

    @Column(name = "off_the_ball")
    private int offTheBall;

    private int positioning;
    private int teamwork;
    private int vision;

    @Column(name = "work_rate")
    private int workRate;

    public Map<String, Integer> getAllMentalAttributes() {
        Map<String, Integer> allMentalAttributes = new HashMap<>();
        allMentalAttributes.put("aggression", aggression);
        allMentalAttributes.put("anticipation", anticipation);
        allMentalAttributes.put("bravery", bravery);
        allMentalAttributes.put("composure", composure);
        allMentalAttributes.put("concentration", concentration);
        allMentalAttributes.put("decisions", decisions);
        allMentalAttributes.put("determination", determination);
        allMentalAttributes.put("flair", flair);
        allMentalAttributes.put("leadership", leadership);
        allMentalAttributes.put("offTheBall", offTheBall);
        allMentalAttributes.put("positioning", positioning);
        allMentalAttributes.put("teamwork", teamwork);
        allMentalAttributes.put("vision", vision);
        allMentalAttributes.put("workRate", workRate);

        return allMentalAttributes;
    }
}
