package com.ksy.fmrs.dto.search;

import lombok.Getter;

import java.util.List;

@Getter
public class SimpleSearchPlayerResultDto {
    private List<SimpleSearchPlayerResponseDto> players;
    private Boolean hasNext;

    public SimpleSearchPlayerResultDto(List<SimpleSearchPlayerResponseDto> players, Boolean hasNext) {
        this.players = players;
        this.hasNext = hasNext;
    }
}
