package com.ksy.fmrs.controller;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.dto.PlayerOverviewDto;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.dto.search.SearchPlayerResponseDto;
import com.ksy.fmrs.service.PlayerFacadeService;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class PlayerController {

    private final PlayerFacadeService playerFacadeService;
    private final PlayerService playerService;

    /**
     * 1. player 상세정보
     * 2. 매핑된 상태라면 fmplayer 정보
     * 3. 실제 스탯
     */
    @GetMapping("/players/{playerId}")
    public String getPlayerDetail(@PathVariable Long playerId, Model model) {
        PlayerOverviewDto playerOverviewDto = playerFacadeService.getPlayerOverview(playerId);
        model.addAttribute("player", playerOverviewDto);
        return "player-detail";
    }

    @GetMapping("/players/detail-search")
    public String searchPlayerByDetailCondition(Model model) {
        SearchPlayerCondition searchPlayerCondition = new SearchPlayerCondition(); // 새로운 객체를 생성하여 모델에 추가
        model.addAttribute("searchPlayerCondition", searchPlayerCondition); // 모델에 추가
        return "players-detail-search";
    }

    // 상세 검색 결과 반환 페이지
    @GetMapping("/players/detail-search/result")
    public String searchPlayerByDetailConditionResult(
            @ModelAttribute("searchPlayerCondition") SearchPlayerCondition searchPlayerCondition, Model model) {
        SearchPlayerResponseDto searchPlayerResponseDto = playerService.searchPlayerByDetailCondition(searchPlayerCondition);
        model.addAttribute("players", searchPlayerResponseDto);
        return "players-detail-search";
    }

    @ResponseBody
    @GetMapping("/api/search/simple-player/{name}")
    public SearchPlayerResponseDto searchPlayerByName(
            @PathVariable String name,
            Pageable pageable,
            @RequestParam(required = false) Long lastPlayerId,
            @RequestParam(required = false) Integer lastCurrentAbility,
            @RequestParam(required = false) MappingStatus lastMappingStatus
    ) {
        return playerService.searchPlayerByName(name, pageable, lastMappingStatus, lastCurrentAbility, lastPlayerId);
    }
}