package com.ksy.fmrs.repository;

import com.ksy.fmrs.config.TestQueryDSLConfig;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.QTeam;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.QPlayer;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Import(TestQueryDSLConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // default 는 h2 사용
class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @AfterEach
    void setUp(){
        playerRepository.deleteAll();
    }

    @Test
    @DisplayName("save 단건")
    void save(){
        // given
        Player player = createPlayer("p1");
        // when
        Player savePlayer = playerRepository.save(player);

        // then
        Assertions.assertThat(savePlayer).isEqualTo(player);
    }

    @Test
    @DisplayName("saveAll 시 select 문 나가는지 테스트")
    void saveAll(){
        // given
        List<Player> playerList = new ArrayList<>();
        Player player = createPlayer("p1");
        Player player2 = createPlayer("p2");
        Team team1 = createTeam("t1");
        Team team2 = createTeam("t2");
        player.updateTeam(team1);
        player2.updateTeam(team2);

        playerList.add(player);
        playerList.add(player2);
        // when
        teamRepository.save(team1);
        teamRepository.save(team2);
        playerRepository.saveAll(playerList);

        // then
    }

    @Test
    @DisplayName("팀id로 소속 선수들 조회")
    void getPlayersByTeamId(){
        // given
        ArrayList<Player> players = new ArrayList<>();
        Team team1 = createTeam("team1");
        Team team2 = createTeam("team2");
        teamRepository.save(team1);
        teamRepository.save(team2);
        for(int i = 0; i < 10; i++){
            Player player = createPlayer("player"+i);
            players.add(player);
            player.updateTeam(team1);
            playerRepository.save(player);
        }
        for(int i = 0; i < 5; i++){
            Player player = createPlayer("not_player"+i);
            players.add(player);
            player.updateTeam(team2);
            playerRepository.save(player);
        }
        // when
        List<Player> actual = playerRepository.findAllByTeamId(team1.getId());
        // then
        actual.forEach(player -> {
            Assertions.assertThat(player.getName()).startsWith("player");
        });
        Assertions.assertThat(actual).hasSize(10);
    }

    @Test
    @DisplayName("상세 검색 테스트 - 팀")
    void detail_search_test(){
        // given
        SearchPlayerCondition condition = new SearchPlayerCondition();
        condition.setTeamName("TOT");
        Team team = Team.builder().name("TOT").build();
        Player player = Player.builder().name("son").age(32).build();
        teamRepository.save(team);
        playerRepository.save(player);
        player.updateTeam(team);

        // when
        List<Player> result = playerRepository.searchPlayerByDetailCondition(condition);
        List<Player> expected = jpaQueryFactory
                .selectFrom(QPlayer.player)
                .leftJoin(QPlayer.player.team, QTeam.team)
                .where(
                        QTeam.team.name.eq(condition.getTeamName())
                )
                .fetch();

        // then
        Assertions.assertThat(result.get(0).getName()).isEqualTo(expected.get(0).getName());
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("몸값 순 선수 모두 조회")
    void findAllOrderByMarketValueDesc(){
        // given
        Player playerA = createPlayer("playerA");
        playerA.updateMarketValue(100);
        Player playerB = createPlayer("playerB");
        playerB.updateMarketValue(200);
        Player playerC = createPlayer("playerC");
        playerC.updateMarketValue(300);
        playerRepository.save(playerA);
        playerRepository.save(playerB);
        playerRepository.save(playerC);

        // when
        List<Player> actual = playerRepository.findAllByOrderByMarketValueDesc();
        // then
        Assertions.assertThat(actual).hasSize(3);
        Assertions.assertThat(actual.get(0).getName()).isEqualTo("playerC");
        Assertions.assertThat(actual.get(1).getName()).isEqualTo("playerB");
        Assertions.assertThat(actual.get(2).getName()).isEqualTo("playerA");
    }

    @Test
    @DisplayName("fmplayerStat으로 player 찾기")
    void searchPlayerByFm(){
        // given
        String fileName = "98031331-Manuel Akanji";
        String name = StringUtils.getPlayerNameFromFileName(fileName);
        String firstName = StringUtils.getFirstName(name).toUpperCase();
        String lastName = StringUtils.getLastName(name).toUpperCase();
        LocalDate birthDate = LocalDate.of(1995,7,19);
        String Nation = "Switzerland".toUpperCase();
        Player player = Player.builder()
                .firstName("MANUEL")
                .lastName("AKANJI")
                .nationName("SWITZERLAND")
                .birth(LocalDate.of(1995,7,19))
                .build();
        playerRepository.save(player);
        // when
        List<Player> result = playerRepository.searchPlayerByFm(firstName, lastName, birthDate, Nation);

        // then
        Assertions.assertThat(result).hasSize(1);
        Player actual = result.get(0);
        Assertions.assertThat(actual.getFirstName()).isEqualTo("MANUEL");
        Assertions.assertThat(actual.getLastName()).isEqualTo("AKANJI");
        Assertions.assertThat(actual.getNationName()).isEqualTo("SWITZERLAND");
        Assertions.assertThat(actual.getBirth()).isEqualTo(LocalDate.of(1995,7,19));

    }

    private Team createTeam(String name){
        return Team.builder().name(name).build();
    }

    private Player createPlayer(String name) {
        return Player.builder().name(name).build();
    }
}