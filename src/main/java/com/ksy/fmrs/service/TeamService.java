package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.team.TeamStandingDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;

    @Transactional
    public void saveAllByTeamStanding(List<TeamStandingDto> teamStandingDtos) {
        // 한 팀이 여러 리그에 속하는 케이스 있기 때문에 일단 중복 리그중 하나는 제거
        List<Team> teams = teamStandingDtos.stream().collect(Collectors.toMap(TeamStandingDto::getTeamApiId,
                dto -> {
                    Team team = Team.builder()
                            .name(dto.getTeamName())
                            .teamApiId(dto.getTeamApiId())
                            .logoUrl(dto.getTeamLogo())
                            .build();
                    League league = leagueRepository.findLeagueByLeagueApiId(dto.getLeagueApiId())
                    .orElseThrow(()-> new RuntimeException("League not found leagueApiId: " + dto.getLeagueApiId()));
                    team.updateLeague(league);
                    return team;
                    }, (existing, replacement) -> existing
                )).values().stream().toList();
        teamRepository.saveAll(teams);
    }

    @Transactional
    public void saveAll(List<Team> teams) {
        teamRepository.saveAll(teams);
    }

    public Team findByTeamApiId(Integer teamApiId) {
        return teamRepository.findTeamByTeamApiId(teamApiId)
                .orElseThrow(() -> new RuntimeException("Team not found teamApiId: " + teamApiId));
    }

}
