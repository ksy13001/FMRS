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
public class TechnicalAttributes {
    private int corners;
    private int crossing;
    private int dribbling;
    private int finishing;
    @Column(name = "first_touch")
    private int firstTouch;
    @Column(name = "free_kick_taking")
    private int freeKicks;
    private int heading;
    @Column(name = "long_shots")
    private int longShots;
    @Column(name = "long_throws")
    private int longThrows;
    private int marking;
    private int passing;
    @Column(name = "penalty_taking")
    private int penaltyTaking;
    private int tackling;
    private int technique;

    public Map<String, Integer> getAllTechnicalAttributes() {
        Map<String, Integer> allPhysicalAttributes = new HashMap<>();
        allPhysicalAttributes.put("corners", corners);
        allPhysicalAttributes.put("crossing", crossing);
        allPhysicalAttributes.put("dribbling", dribbling);
        allPhysicalAttributes.put("finishing", finishing);
        allPhysicalAttributes.put("firstTouch", firstTouch);
        allPhysicalAttributes.put("freeKicks", freeKicks);
        allPhysicalAttributes.put("heading", heading);
        allPhysicalAttributes.put("long shots", longShots);
        allPhysicalAttributes.put("long throws", longThrows);
        allPhysicalAttributes.put("marking", marking);
        allPhysicalAttributes.put("passing", passing);
        allPhysicalAttributes.put("penalty taking", penaltyTaking);
        allPhysicalAttributes.put("tackling", tackling);
        allPhysicalAttributes.put("technique", technique);
        return allPhysicalAttributes;
    }
}
