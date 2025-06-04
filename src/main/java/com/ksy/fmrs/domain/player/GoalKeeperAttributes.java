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
public class GoalKeeperAttributes {
    @Column(name = "aerial_ability")
    private int aerialAbility;
    @Column(name = "command_of_area")
    private int commandOfArea;
    private int communication;
    private int eccentricity;
    private int handling;
    private int kicking;
    @Column(name = "one_on_ones")
    private int oneOnOnes;
    private int reflexes;
    @Column(name = "rushing_out")
    private int rushingOut;
    @Column(name = "tendency_to_punch")
    private int tendencyToPunch;
    private int throwing;

    public Map<String, Integer> getAllGoalkeeperAttributes() {
        Map<String, Integer> allAttributes = new HashMap<>();
        allAttributes.put("aerial ability", aerialAbility);
        allAttributes.put("command of area", commandOfArea);
        allAttributes.put("communication", communication);
        allAttributes.put("eccentricity", eccentricity);
        allAttributes.put("handling", handling);
        allAttributes.put("kicking", kicking);
        allAttributes.put("one on ones", oneOnOnes);
        allAttributes.put("reflexes", reflexes);
        allAttributes.put("rushing out", rushingOut);
        allAttributes.put("tendency to punch", tendencyToPunch);
        allAttributes.put("throwing", throwing);
        return allAttributes;
    }
}
