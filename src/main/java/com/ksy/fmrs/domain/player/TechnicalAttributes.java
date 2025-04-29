package com.ksy.fmrs.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

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
}
