package com.ksy.fmrs.dto.player;

import com.ksy.fmrs.domain.player.*;

public record FmPlayerDetailsDto(
        String name,
        Position position,
        int currentAbility,
        int potentialAbility,
        PersonalityAttributes personalityAttributes,
        TechnicalAttributes technicalAttributes,
        MentalAttributes mentalAttributes,
        PhysicalAttributes physicalAttributes,
        GoalKeeperAttributes goalKeeperAttributes,
        HiddenAttributes hiddenAttributes
) {
    public FmPlayerDetailsDto(FmPlayer p) {
        this(
                p.getName(),
                p.getPosition(),
                p.getCurrentAbility(),
                p.getPotentialAbility(),
                p.getPersonalityAttributes(),
                p.getTechnicalAttributes(),
                p.getMentalAttributes(),
                p.getPhysicalAttributes(),
                p.getGoalKeeperAttributes(),
                p.getHiddenAttributes()
        );
    }
}
