package com.ksy.fmrs.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class SearchPlayersResultDto {

    private List<DetailSearchPlayerResponseDto> players;
    private Boolean hasNext;
    private Integer totalPages;
    private Long totalElements;

    public static SearchPlayersResultDto fromSlice(
            List<DetailSearchPlayerResponseDto> players, Boolean hasNext
    ){
        return new SearchPlayersResultDto(players, hasNext, null, null);
    }

    public static SearchPlayersResultDto fromPage(
            List<DetailSearchPlayerResponseDto> players, Integer totalPages, Long totalElements
    ){
        return new SearchPlayersResultDto(players, null, totalPages, totalElements);
    }
}
