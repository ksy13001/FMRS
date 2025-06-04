package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.config.TestQueryDSLConfig;
import com.ksy.fmrs.config.TestTimeProviderConfig;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Import(TestQueryDSLConfig.class)
@DataJpaTest
class FmPlayerRepositoryTest {

    @Autowired
    private FmPlayerRepository fmPlayerRepository;

    @Autowired
    private TestEntityManager tem;

    @Test
    @DisplayName("fm 데이터 복합키로 find 테스트")
    void findFmPlayerByFirstNameAndLastNameAndBirthAndNationName() {
        // given
        LocalDate date = LocalDate.now();
        FmPlayer fm1 = createFmPlayer("fm1", "fm2", date, "n1");
        tem.persistAndFlush(fm1);
        // when
        List<FmPlayer> fmPlayerList = fmPlayerRepository.findFmPlayerByFirstNameAndLastNameAndBirthAndNationName(
                "fm1",
                "fm2",
                date,
                "n1"
        );
        // then
        Assertions.assertThat(fmPlayerList.getFirst()).isEqualTo(fm1);
    }

    @Test
    @DisplayName("1000 개 player 매핑 성능 테스트")
    void mapping_player_test(){
        // given
        LocalDate date = LocalDate.now();
        for(int i=0;i<1000;i++){
            Player player = createPlayer("fn"+i, "ln"+i, date, "nation"+i);
            FmPlayer fmPlayer = createFmPlayer("fn"+i, "ln"+i, date, "nation"+i);
            tem.persist(player);
            tem.persist(fmPlayer);
        }
        tem.flush();
        tem.clear();
        List<FmPlayer> actual = new ArrayList<>();
        // when
        long startTime = System.currentTimeMillis();
        for(int j=0;j<1000;j++){
            List<FmPlayer> result = fmPlayerRepository.findFmPlayerByFirstNameAndLastNameAndBirthAndNationName(
                    "fn"+j, "ln"+j, date, "nation"+j
            );
            if(result.size()==1){
                actual.add(result.getFirst());
            }
        }
        long endTime = System.currentTimeMillis();

        // then
        Assertions.assertThat(actual).hasSize(1000);
        System.out.println("실행 시간 : " + (endTime-startTime) + "ms");
        // 인덱스 미적용 시: 319272ms - 5분 32초
        // 인덱스 적용 시 : 6335ms - 6초
    }

    @Test
    @DisplayName("테스트설명")
    void 메서드명() throws Exception{
        // given

        // when

        // then
    }

    private Player createPlayer(String firstName, String lastName, LocalDate birth, String nation){
        return Player.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birth(birth)
                .nationName(nation)
                .mappingStatus(MappingStatus.UNMAPPED)
                .build();
    }

    private FmPlayer createFmPlayer(String firstName, String lastName, LocalDate birth, String nation) {
        return FmPlayer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birth(birth)
                .nationName(nation)
                .position(Position.builder()
                        .goalkeeper(0)
                        .defenderCentral(0)
                        .defenderLeft(0)
                        .defenderRight(0)
                        .wingBackLeft(0)
                        .wingBackRight(0)
                        .defensiveMidfielder(0)
                        .midfielderLeft(0)
                        .midfielderRight(0)
                        .midfielderCentral(0)
                        .attackingMidCentral(10)
                        .attackingMidLeft(5)
                        .attackingMidRight(8)
                        .striker(20)
                        .build())
                .personalityAttributes(PersonalityAttributes.builder()
                        .adaptability(15)
                        .ambition(18)
                        .loyalty(20)
                        .pressure(17)
                        .professional(19)
                        .sportsmanship(16)
                        .temperament(15)
                        .controversy(3)
                        .build())
                .technicalAttributes(TechnicalAttributes.builder()
                        .corners(14)
                        .crossing(18)
                        .dribbling(20)
                        .finishing(20)
                        .firstTouch(20)
                        .freeKicks(18)
                        .heading(12)
                        .longShots(18)
                        .longThrows(5)
                        .marking(10)
                        .passing(19)
                        .penaltyTaking(18)
                        .tackling(10)
                        .technique(20)
                        .build())
                .mentalAttributes(MentalAttributes.builder()
                        .aggression(10)
                        .anticipation(20)
                        .bravery(15)
                        .composure(18)
                        .concentration(16)
                        .decisions(19)
                        .determination(20)
                        .flair(20)
                        .leadership(13)
                        .offTheBall(20)
                        .positioning(13)
                        .teamwork(17)
                        .vision(20)
                        .workRate(15)
                        .build())
                .physicalAttributes(PhysicalAttributes.builder()
                        .acceleration(19)
                        .agility(19)
                        .balance(20)
                        .jumpingReach(8)
                        .naturalFitness(17)
                        .pace(19)
                        .stamina(17)
                        .strength(13)
                        .build())
                .goalKeeperAttributes(GoalKeeperAttributes.builder()
                        .aerialAbility(1)
                        .commandOfArea(1)
                        .communication(1)
                        .eccentricity(1)
                        .handling(1)
                        .kicking(1)
                        .oneOnOnes(1)
                        .reflexes(1)
                        .rushingOut(1)
                        .tendencyToPunch(1)
                        .throwing(1)
                        .build())
                .hiddenAttributes(HiddenAttributes.builder()
                        .consistency(19)
                        .dirtiness(3)
                        .importantMatches(20)
                        .injuryProneness(8)
                        .versatility(15)
                        .build())
                .currentAbility(190)
                .potentialAbility(195)
                .build();
    }
}