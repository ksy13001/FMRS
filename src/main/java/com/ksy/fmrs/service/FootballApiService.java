package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.PlayerStat;
import com.ksy.fmrs.dto.PlayerStatDto;
import com.ksy.fmrs.dto.PlayerStatisticsApiResponseDto;
import com.ksy.fmrs.dto.TeamApiResponseDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.PlayerStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FootballApiService {

    @Value("${api-football.key}")
    private String apiFootballKey;
    @Value("${api-football.host}")
    private String apiFootballHost;

    // 기본 URL 및 쿼리 파라미터 상수
    private static final String TEAM_URL = "https://v3.football.api-sports.io/teams?";
    private static final String PLAYER_STAT_URL = "https://v3.football.api-sports.io/players?";
    private static final String PARAM_NAME = "name=";
    private static final String PARAM_SEARCH = "search=";
    private static final String PARAM_TEAM = "team=";
    private static final String AND = "&";

    private final PlayerStatRepository  playerStatRepository;
    private final PlayerRepository playerRepository;
    /**
     * 1. playerDetailDto 에서 playerName, teamName 가져옴
     * 2. teamName 을 통해 api-football 에서 teamApiId 가져옴
     * 3. playerName + teamApiId 를 통해 api-football 에서 playerApi + playerRealStat 가져옴
     * */
    @Transactional
    public PlayerStatDto getPlayerRealStat(Long playerId, String playerName, String teamName) {

        return getOptionalPlayerStatById(playerId)
                .map(PlayerStatDto::new)
                .orElseGet(()->{
                    String url = buildPlayerStatUrl(playerName, getTeamApiIdByTeamName(teamName));
                    ResponseEntity<PlayerStatisticsApiResponseDto> response = getApiResponse(url, PlayerStatisticsApiResponseDto.class);
                    PlayerStatDto playerStatDto = getPlayerStatFromStatistics(Objects.requireNonNull(response.getBody()));
                    updatePlayerImage(playerId, playerStatDto.getImageUrl());
                    savePlayerStat(playerStatDtoToPlayerStat(playerId, playerStatDto));
                    return playerStatDto;
                });
    }

    // 출장경기수, 골, 어시스트, 평점, 선수 이미지
    private PlayerStatDto getPlayerStatFromStatistics(PlayerStatisticsApiResponseDto response) {
        PlayerStatisticsApiResponseDto.StatisticDto stat = response.getResponse().getFirst().getStatistics().getFirst();
        PlayerStatDto playerRealStatDto = new PlayerStatDto();
        playerRealStatDto.setApiFootballId(response.getResponse().getFirst().getPlayer().getId());
        playerRealStatDto.setGamesPlayed(stat.getGames().getAppearences());
        playerRealStatDto.setGoal(stat.getGoals().getTotal());
        playerRealStatDto.setAssist(stat.getGoals().getAssists());
        playerRealStatDto.setPk(stat.getPenalty().getScored());
        playerRealStatDto.setRating(truncateToTwoDecimalsRanging(stat.getGames().getRating()));
        playerRealStatDto.setImageUrl(response.getResponse().getFirst().getPlayer().getPhoto());
        return playerRealStatDto;
    }

    private String truncateToTwoDecimalsRanging(String r) {
        double rating = Double.parseDouble(r);
        return String.format("%.2f", rating);
    }

    private Integer getTeamApiIdByTeamName(String teamName){
        String url = buildTeamUrl(teamName);

        ResponseEntity<TeamApiResponseDto> teamApiResponseDto = getApiResponse(url,  TeamApiResponseDto.class);
        return Objects.requireNonNull(teamApiResponseDto.getBody())
                .getResponse()
                .getFirst()
                .getTeam()
                .getId();
    }

    // API 호출 공통 로직
    private <T> ResponseEntity<T> getApiResponse(String url, Class<T> responseType) {
        return createAPIFootballRestClient()
                .get()
                .uri(url)
                .retrieve()
                .toEntity(responseType);
    }

    RestClient createAPIFootballRestClient(){
        return RestClient.builder()
                .defaultHeader("X-RapidAPI-Key", apiFootballKey)
                .defaultHeader("X-RapidAPI-Host", apiFootballHost)
                .build();
    }

    // 선수 통계 API URL을 생성 (playerName, teamApiId, 시즌)
    private String buildPlayerStatUrl(String playerName, Integer teamApiId) {
        return PLAYER_STAT_URL + PARAM_SEARCH + playerName + AND + PARAM_TEAM + teamApiId;
    }

    // 팀 API URL을 생성 (teamName)
    private String buildTeamUrl(String teamName) {
        return TEAM_URL + PARAM_NAME + teamName;
    }

    private PlayerStat playerStatDtoToPlayerStat(Long playerId, PlayerStatDto playerStatDto) {
        return PlayerStat.builder()
                .playerId(playerId)
                .apiFootballId(playerStatDto.getApiFootballId())
                .gamesPlayed(playerStatDto.getGamesPlayed())
                .goal(playerStatDto.getGoal())
                .assist(playerStatDto.getAssist())
                .pk(playerStatDto.getPk())
                .rating(playerStatDto.getRating())
                .imageUrl(playerStatDto.getImageUrl())
                .build();
    }

    private void updatePlayerImage(Long playerId, String imageUrl) {
        Player player = findPlayerById(playerId);
        player.updateImageUrl(imageUrl);
    }

    private void savePlayerStat(PlayerStat playerStat){
        playerStatRepository.save(playerStat);
    }

    private Optional<PlayerStat> getOptionalPlayerStatById(Long playerStatId){
        return playerStatRepository.findById(playerStatId);
    }

    private Player findPlayerById(Long playerId){
        return playerRepository.findById(playerId).orElseThrow(
                ()->new IllegalArgumentException("Player with id "+playerId+" not found")
        );
    }
}