package com.ksy.fmrs.dto.search;

import com.ksy.fmrs.domain.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DetailSearchPlayerResponseDto {
    private Long id;
    private String name;
    private String teamName;
    private String teamLogoUrl;
    private String nationName;
    private Integer age;
    private Integer currentAbility;
    private String imageUrl;
    private List<String> topAttributes;

    public DetailSearchPlayerResponseDto(Player player, Integer currentAbility, List<String> topAttributes) {
        this.id = player.getId();
        this.name = player.getName();
        this.teamName = player.getTeamName();
        this.teamLogoUrl = player.getTeamLogoUrl();
        this.nationName = player.getNationName();
        this.age = player.getAge();
        this.currentAbility = currentAbility;
        this.imageUrl = player.getImageUrl();
        this.topAttributes = topAttributes;
    }
}
