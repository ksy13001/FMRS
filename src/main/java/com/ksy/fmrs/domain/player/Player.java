package com.ksy.fmrs.domain.player;


import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.util.time.TimeUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_api_id", unique = true)
    private Integer playerApiId;

    private String name;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private LocalDate birth;

    private int height;

    private int weight;

    @Column(name = "nation_name")
    private String nationName;

    @Column(name = "nation_logo_url")
    private String nationLogoUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="mapping_status")
    private MappingStatus mappingStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fmplayer_id", unique = true)
    private FmPlayer fmPlayer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_stat_id", unique = true)
    private PlayerStat playerStat;

    @Column(name = "is_gk")
    private Boolean isGK;

    @Builder
    public Player(
            Integer playerApiId,
            String firstName,
            String lastName,
            String name,
            LocalDate birth,
            int height,
            int weight,
            String imageUrl,
            String nationName,
            String nationLogoUrl,
            MappingStatus mappingStatus,
            Boolean isGK
    ) {
        this.playerApiId = playerApiId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.birth = birth;
        this.height = height;
        this.weight = weight;
        this.imageUrl = imageUrl;
        this.nationName = nationName;
        this.nationLogoUrl = nationLogoUrl;
        this.mappingStatus = mappingStatus;
        this.isGK = isGK;
    }

    // 연관관계 설정 메서드
    public void updateTeam(Team team) {
        this.team = team;
        team.getPlayers().add(this);
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateFmPlayer(FmPlayer fmPlayer) {
        this.fmPlayer = fmPlayer;
    }

    public void updateMappingStatus(MappingStatus status) {
        this.mappingStatus = status;
    }

    public int getAge(){
        return TimeUtils.getAge(this.birth);
    }

    public void updatePlayerApiId(Integer playerApiId) {
        this.playerApiId = playerApiId;
    }

    public void updatePlayerStat(PlayerStat playerStat) {
        this.playerStat = playerStat;
    }

    public String getStringBirth(){
        return String.valueOf(birth);
    }

    public String getTeamName(){
        if(this.team == null){
            return null;
        }
        return this.team.getName();
    }

    public String getTeamLogoUrl(){
        if(this.team == null){
            return null;
        }
        return this.team.getLogoUrl();
    }

    public Integer getFmPlayerCurrentAbility(){
        if(this.fmPlayer == null){
            return null;
        }
        return this.fmPlayer.getCurrentAbility();
    }

    public boolean isMatched(){
        return this.fmPlayer != null && this.mappingStatus == MappingStatus.MATCHED;
    }
}
