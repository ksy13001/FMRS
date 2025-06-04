package com.ksy.fmrs.dto.search;

import lombok.Getter;

import java.util.List;

@Getter
public class DetailSearchPlayerResultDto {
    private List<DetailSearchPlayerResponseDto> players;
    private Integer totalPages;
    private Long totalElements;

    public DetailSearchPlayerResultDto(List<DetailSearchPlayerResponseDto> players, Integer totalPages, Long totalElements) {
        this.players = players;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
