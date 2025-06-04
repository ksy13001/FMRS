package com.ksy.fmrs.controller;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.dto.nation.NationDto;
import com.ksy.fmrs.dto.player.PlayerOverviewDto;
import com.ksy.fmrs.dto.search.DetailSearchPlayerResultDto;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.dto.search.SearchPlayersResultDto;
import com.ksy.fmrs.dto.search.SimpleSearchPlayerResultDto;
import com.ksy.fmrs.service.PlayerFacadeService;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PlayerController {

    private final PlayerFacadeService playerFacadeService;
    private final PlayerService playerService;

    /**
     * 1. player 상세정보
     * 2. 매핑된 상태라면 fmplayer 정보
     * 3. 실제 스탯
     */
    @GetMapping("/api/players/{playerId}")
    public PlayerOverviewDto getPlayerDetail(@PathVariable Long playerId) {
        return playerFacadeService.getPlayerOverview(playerId);
    }

    @GetMapping("/api/search/simple-player/{name}")
    public SimpleSearchPlayerResultDto searchPlayerByName(
            @PathVariable String name,
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Long lastPlayerId,
            @RequestParam(required = false) Integer lastCurrentAbility,
            @RequestParam(required = false) MappingStatus lastMappingStatus
    ) {
        return playerService.simpleSearchPlayers(name, pageable, lastPlayerId, lastCurrentAbility, lastMappingStatus);
    }

    @GetMapping("/api/search/detail-player")
    public DetailSearchPlayerResultDto searchPlayerByDetailConditionResult(
            @ModelAttribute SearchPlayerCondition searchPlayerCondition,
            @PageableDefault Pageable pageable
    ) {
        return playerService.detailSearchPlayers(searchPlayerCondition, pageable);
    }

    @GetMapping("/api/nations")
    public List<NationDto> getNationsFromPlayers() {
        return playerService.getNationsFromPlayers();
    }
}