package com.ksy.fmrs.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

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
}
