package com.ksy.fmrs.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PhysicalAttributes {
    private int acceleration;
    private int agility;
    private int balance;

    @Column(name = "jumping_reach")
    private int jumpingReach;

    @Column(name = "natural_fitness")
    private int naturalFitness;
    private int pace;
    private int stamina;
    private int strength;

    public Map<String, Integer> getAllPhysicalAttributes(){
        Map<String, Integer> allAttributes = new HashMap<>();
        allAttributes.put("acceleration", acceleration);
        allAttributes.put("agility", agility);
        allAttributes.put("balance", balance);
        allAttributes.put("jumping_reach", jumpingReach);
        allAttributes.put("natural_fitness", naturalFitness);
        allAttributes.put("pace", pace);
        allAttributes.put("stamina", stamina);
        allAttributes.put("strength", strength);
        return allAttributes;
    }
}