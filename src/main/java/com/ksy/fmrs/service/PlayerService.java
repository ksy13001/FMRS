package com.ksy.fmrs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.*;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.apiFootball.LeagueApiPlayersDto;
import com.ksy.fmrs.dto.nation.NationDto;
import com.ksy.fmrs.dto.player.FmPlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.search.*;
import com.ksy.fmrs.dto.team.TeamPlayersResponseDto;
import com.ksy.fmrs.mapper.PlayerMapper;
import com.ksy.fmrs.repository.BulkRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerService {

    private static final Integer TOP_N = 3;

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final BulkRepository bulkRepository;
    private final ObjectMapper objectMapper;
    private final PlayerMapper playerMapper;

    /**
     * 선수 상세 정보 조회
     */
    @Transactional(readOnly = true)
    public PlayerDetailsDto getPlayerDetails(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        return convertPlayerToPlayerDetailsDto(player);
    }

    @Transactional(readOnly = true)
    public Optional<FmPlayerDetailsDto> getFmPlayerDetails(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        if (player.getMappingStatus() != MappingStatus.MATCHED) {
            return Optional.empty();
        }
        return Optional.of(new FmPlayerDetailsDto(player.getFmPlayer()));
    }

    @Transactional
    public void saveAllByPlayerStatistics(LeagueApiPlayersDto leagueApiPlayersDto) {
        List<Player> players = leagueApiPlayersDto.response().stream().filter(Objects::nonNull)
                .map(dto -> {
                    LeagueApiPlayersDto.PlayerDto player = dto.player();
                    LeagueApiPlayersDto.StatisticDto.TeamDto teamDto = dto.statistics().getFirst().team();
                    Team team = teamRepository.findTeamByTeamApiId(teamDto.id())
                            .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamDto.id()));
                    Player newPlayer = Player.builder()
//                            .name(player.name())
                            .playerApiId(player.id())
//                            .teamApiId(Objects.requireNonNull(dto.statistics()).getFirst().team().id())
//                            .leagueApiId(Objects.requireNonNull(dto.statistics()).getFirst().league().id())
                            .firstName(StringUtils.getFirstName(player.firstname()).toUpperCase())
                            .lastName(StringUtils.getLastName(player.lastname()).toUpperCase())
                            .nationName(player.nationality().toUpperCase())
                            .nationLogoUrl(Objects.requireNonNull(dto.statistics().getFirst().league().flag()))
//                            .age(player.age())
                            .birth(player.birth().date())
                            .height(StringUtils.extractNumber(player.height()))
                            .weight(StringUtils.extractNumber(player.weight()))
                            .build();
                    newPlayer.updateTeam(team);
                    return newPlayer;
                }).toList();
        playerRepository.saveAll(players);
    }

    @Transactional
    public void bulkInsertPlayers(List<Player> players) {
        // 중복 제거된 Player 리스트를 저장
        bulkRepository.bulkInsertPlayers(getDistinctPlayersByPlayerApiId(players));
    }

//    @Transactional
//    public Long updateDuplicatedUnmappedPlayersToFailed(){
//        return playerRepository.updateDuplicatedUnmappedPlayersToFailed();
//    }

    /**
     * playerRaw로 선수 저장: 한 페이지 별 저장
     */
    @Transactional
    public void savePlayersByPlayerRaw(PlayerRaw playerRaw) throws JsonProcessingException {
        LeagueApiPlayersDto leagueApiPlayersDto = objectMapper.readValue(
                playerRaw.getJsonRaw(), LeagueApiPlayersDto.class);
        bulkRepository.bulkInsertPlayers(playerMapper.leaguePlayersToEntities(leagueApiPlayersDto));
    }

    private List<Player> getDistinctPlayersByPlayerApiId(List<Player> players) {
        return players.stream()
                .collect(Collectors.toMap(
                        Player::getPlayerApiId,          // keyMapper: playerApiId를 key 로
                        Function.identity(),             // valueMapper: Player 객체 자체
                        (existing, replacement) -> existing // mergeFunction: key 충돌 시 기존 객체 사용
                ))
                .values()
                .stream()
                .toList();
    }

    private PlayerDetailsDto convertPlayerToPlayerDetailsDto(Player player) {
        return new PlayerDetailsDto(
                player,
                player.getTeamName(),
                player.getTeamLogoUrl(),
                player.getFmPlayerCurrentAbility()
        );
    }

    /**
     * 팀 소속 선수들 모두 조회
     */
    @Transactional(readOnly = true)
    public TeamPlayersResponseDto getTeamPlayersByTeamId(Long teamId) {
        return new TeamPlayersResponseDto(playerRepository.findAllByTeamId(teamId)
                .stream()
                .map(this::convertPlayerToPlayerDetailsDto)
                .toList());
    }

    /**
     * 선수 이름 검색
     */
    @Transactional(readOnly = true)
    public SimpleSearchPlayerResultDto simpleSearchPlayers(
            String name,
            Pageable pageable,
            Long lastPlayerId,
            Integer lastCurrentAbility,
            MappingStatus lastMappingStatus
    ) {
        Slice<Player> result = playerRepository.searchPlayerByName(
                name, pageable,lastPlayerId, lastCurrentAbility, lastMappingStatus);
        return new SimpleSearchPlayerResultDto(
                result.getContent().stream().map(this::convertPlayerToSimpleSearchPlayerResponseDto).toList(),
                result.hasNext()
        );
    }

    /**
     * 선수 상세 검색
     */
    @Transactional(readOnly = true)
    public DetailSearchPlayerResultDto detailSearchPlayers(
            SearchPlayerCondition condition,
            Pageable pageable
    ) {
        Page<Player> result = playerRepository.searchPlayerByDetailCondition(condition, pageable);
        return new DetailSearchPlayerResultDto(
                result.getContent().stream().map(this::convertPlayerToDetailSearchPlayerResponseDto).toList(),
                result.getTotalPages(),
                result.getTotalElements()
        );
    }

    private DetailSearchPlayerResponseDto convertPlayerToDetailSearchPlayerResponseDto(Player player) {
        if(player.isMatched()){
            FmPlayer fmPlayer = player.getFmPlayer();
            return new DetailSearchPlayerResponseDto(player,
                    fmPlayer.getCurrentAbility(),
                    fmPlayer.getTopNAttributes(TOP_N, fmPlayer.getAllAttributes()));
        }
        return new DetailSearchPlayerResponseDto(player,
                null,
                Collections.emptyList()
                );
    }

    private SimpleSearchPlayerResponseDto convertPlayerToSimpleSearchPlayerResponseDto(Player player) {
        if(player.isMatched()){
            FmPlayer fmPlayer = player.getFmPlayer();
            return new SimpleSearchPlayerResponseDto(player,
                    fmPlayer.getCurrentAbility());
        }
        return new SimpleSearchPlayerResponseDto(
                player,
                null
        );
    }
    /**
     *  TOP N 개 능력치 반환
     * */
    @Transactional(readOnly = true)
    public List<String> getTopNAttributes(Long playerId, int n) {
        Player player =  playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found: " + playerId));

        if(!player.isMatched()){
            return Collections.emptyList();
        }

        FmPlayer fmPlayer = player.getFmPlayer();
        Map<String, Integer> allAttributes = fmPlayer.getAllAttributes();

        if(n<=0 || n > allAttributes.size()){
            return Collections.emptyList();
        }
        return fmPlayer.getTopNAttributes(n, allAttributes);
    }


    /**
     * 하나의 player 에 대응되는 fmPlayer 가 여러개인 경우 Failed 처리
     */
    @Transactional
    public void updatePlayersMappingStatusToFailed(List<Player> players) {
        players.forEach(player -> {
            log.info("id:" + player.getId());
            player.updateMappingStatus(MappingStatus.FAILED);
        });
    }

    @Transactional(readOnly = true)
    public List<Player> getPlayersWithMultipleFmPlayers() {
        return playerRepository.findPlayerDuplicatedWithFmPlayer();
    }

    @Transactional(readOnly = true)
    public List<Player> getDuplicatePlayers() {
        return playerRepository.findDuplicatedPlayers();
    }

    @Transactional(readOnly = true)
    public List<NationDto> getNationsFromPlayers() {
        return playerRepository.getNationNamesFromPlayers().stream().map(NationDto::new).toList();
    }
}
