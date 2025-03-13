package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.player.*;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.apiFootball.PlayerStatisticsApiResponseDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.dto.search.SearchPlayerResponseDto;
import com.ksy.fmrs.dto.team.TeamPlayersResponseDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * 선수 상세 정보 조회
     * */
    public PlayerDetailsDto getPlayerDetails(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        return convertPlayerToPlayerDetailsResponseDto(player);
    }

    private PlayerDetailsDto convertPlayerToPlayerDetailsResponseDto(Player player) {
        return new PlayerDetailsDto(player,  getTeamNameByPlayer(player));
    }

    private String getTeamNameByPlayer(Player player) {
        return Optional.ofNullable(player.getTeam())
                .map(Team::getName)
                .orElse(null);
    }

    @Transactional
    public void updatePlayerApiIdByPlayerWrapperDto(PlayerStatisticsApiResponseDto.PlayerWrapperDto playerWrapperDto) {
        PlayerStatisticsApiResponseDto.PlayerDto squadPlayer = playerWrapperDto.getPlayer();
        String firstName = StringUtils.getFirstName(squadPlayer.getFirstname());
        String lastName = StringUtils.getLastName(squadPlayer.getName());
        LocalDate birth = StringUtils.parseLocalToString(squadPlayer.getBirth().getDate());
        List<Player> findPlayers = playerRepository.searchPlayerByLastNameAndBirth(lastName, birth, firstName);
        if (findPlayers.size() > 1) {
            log.info("------ max_size error----: name:" + squadPlayer.getName()+"  birth:" + birth +  " squadPlayer:" + squadPlayer.getName());
            return;
        }
        if (findPlayers.isEmpty()) {
            log.info("------ empty error: name:" + squadPlayer.getName()+"  birth:" + birth +  " squadPlayer first:" + squadPlayer.getFirstname());
            return;
        }
        log.info("!!!!!! success name: " + squadPlayer.getName() + " api_idL " + squadPlayer.getId() );
        findPlayers.getFirst().updatePlayerApiId(squadPlayer.getId());
    }

    /**
     * 팀 소속 선수들 모두 조회
     * */
    public TeamPlayersResponseDto getTeamPlayersByTeamId(Long teamId) {
        return new TeamPlayersResponseDto(playerRepository.findAllByTeamId(teamId)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }

    /**
     * 모든 선수 몸값순 조회
     * */
    public SearchPlayerResponseDto getPlayersByMarketValueDesc() {
        return new SearchPlayerResponseDto(playerRepository.findAllByOrderByMarketValueDesc()
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }


    /**
     * 선수 이름 검색
     * */
    public SearchPlayerResponseDto searchPlayerByName(String name) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByName(name)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }

    /**
     * 선수 상세 조회
     * */
    public SearchPlayerResponseDto searchPlayerByDetailCondition(SearchPlayerCondition condition) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByDetailCondition(condition)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }

}
