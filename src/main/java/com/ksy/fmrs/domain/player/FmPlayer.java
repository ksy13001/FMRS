package com.ksy.fmrs.domain.player;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.util.FmUtils;
import com.ksy.fmrs.util.NationNormalizer;
import com.ksy.fmrs.util.StringUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "fmplayer")
//@Table(name = "fmplayer", indexes = @Index(name = "idx_first_name_and_last_name_and_birth_and_nation_name",
//        columnList = "first_name, last_name, birth, nation_name"))
public class FmPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private LocalDate birth;

    @Column(name = "nation_name")
    private String nationName;

    @Enumerated(EnumType.STRING)
    private MappingStatus mappingStatus;

    @Embedded
    private Position position;

    @Column(name = "current_ability")
    private Integer currentAbility;

    @Column(name = "potential_ability")
    private Integer potentialAbility;

    // 인성
    @Column(name = "personality_attributes")
    @Embedded
    private PersonalityAttributes personalityAttributes;

    // 기술(Technical) 능력치
    @Column(name = "technical_attributes")
    @Embedded
    private TechnicalAttributes technicalAttributes;

    // 정신(Mental) 능력치
    @Column(name = "mental_attributes")
    @Embedded
    private MentalAttributes mentalAttributes;

    // 신체(Physical) 능력치
    @Column(name = "physical_attributes")
    @Embedded
    private PhysicalAttributes physicalAttributes;

    // 골키퍼 능력치
    @Column(name = "goalKeeper_attributes")
    @Embedded
    private GoalKeeperAttributes goalKeeperAttributes;

    // 히든 능력치
    @Column(name = "hidden_attributes")
    @Embedded
    private HiddenAttributes hiddenAttributes;

    @Builder
    public FmPlayer(
            String name,
            String firstName,
            String lastName,
            LocalDate birth,
            String nationName,
            Position position,
            PersonalityAttributes personalityAttributes,
            TechnicalAttributes technicalAttributes,
            MentalAttributes mentalAttributes,
            PhysicalAttributes physicalAttributes,
            GoalKeeperAttributes goalKeeperAttributes,
            HiddenAttributes hiddenAttributes,
            Integer currentAbility,
            Integer potentialAbility) {
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birth = birth;
        this.nationName = nationName;
        this.position = position;
        this.personalityAttributes = personalityAttributes;
        this.technicalAttributes = technicalAttributes;
        this.mentalAttributes = mentalAttributes;
        this.physicalAttributes = physicalAttributes;
        this.goalKeeperAttributes = goalKeeperAttributes;
        this.hiddenAttributes = hiddenAttributes;
        this.currentAbility = currentAbility;
        this.potentialAbility = potentialAbility;
    }

    public static FmPlayer FmPlayerDtoToEntity(FmPlayerDto fmPlayerDto) {
        String name = fmPlayerDto.getName();
        return FmPlayer.builder()
                .name(name)
                .firstName(StringUtils.getFirstName(name).toUpperCase())
                .lastName(StringUtils.getLastName(name).toUpperCase())
                .birth(fmPlayerDto.getBorn())
                .nationName(NationNormalizer.normalize(fmPlayerDto.getNation().getName().toUpperCase()))
                .personalityAttributes(FmUtils.getPersonalityAttributesFromFmPlayer(fmPlayerDto))
                .hiddenAttributes(FmUtils.getHiddenAttributesFromFmPlayer(fmPlayerDto))
                .mentalAttributes(FmUtils.getMentalAttributesFromFmPlayer(fmPlayerDto))
                .physicalAttributes(FmUtils.getPhysicalAttributesFromFmPlayer(fmPlayerDto))
                .goalKeeperAttributes(FmUtils.getGoalKeeperAttributesFromFmPlayer(fmPlayerDto))
                .technicalAttributes(FmUtils.getTechnicalAttributesFromFmPlayer(fmPlayerDto))
                .position(FmUtils.getPositionFromFmPlayer(fmPlayerDto))
                .currentAbility(fmPlayerDto.getCurrentAbility())
                .potentialAbility(fmPlayerDto.getPotentialAbility())
                .build();
    }

    public Map<String, Integer> getAllAttributes() {
        Map<String, Integer> allAttributes = new HashMap<>();
        allAttributes.putAll(this.getTechnicalAttributes().getAllTechnicalAttributes());
        allAttributes.putAll(this.getMentalAttributes().getAllMentalAttributes());
        allAttributes.putAll(this.getPhysicalAttributes().getAllPhysicalAttributes());
        allAttributes.putAll(this.getGoalKeeperAttributes().getAllGoalkeeperAttributes());

        return allAttributes;

    }

    public List<String> getTopNAttributes(int n, Map<String, Integer> allAttributes) {
        List<String> topNAttributes  = allAttributes.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .toList();

        return topNAttributes.subList(0, Math.min(n, topNAttributes.size()));
    }

    public void updateName(String name) {
        this.name = name;
    }
}
