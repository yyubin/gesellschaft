package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.skill.SkillStatsBySync;
import model.skill.SyncLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * SkillStatsBySync JPA 엔티티
 * - 동기화 레벨별 스킬 스탯
 * - 스킬 효과와 코인 정보 포함
 */
@Entity
@Table(name = "skill_stats_by_sync")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SkillStatsBySyncJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private SyncLevel syncLevel;  // SYNC_1, SYNC_2, SYNC_3, SYNC_4

    private int basePower;

    private int coinPower;

    private int coinCount;

    private int weight;

    private int level;

    // 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private SkillJpa skill;

    @OneToMany(mappedBy = "statsBySync", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillDescriptionJpa> skillEffects = new ArrayList<>();

    @OneToMany(mappedBy = "statsBySync", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillCoinJpa> skillCoins = new ArrayList<>();

    @Builder
    public SkillStatsBySyncJpa(SyncLevel syncLevel, int basePower, int coinPower,
                               int coinCount, int weight, int level) {
        this.syncLevel = syncLevel;
        this.basePower = basePower;
        this.coinPower = coinPower;
        this.coinCount = coinCount;
        this.weight = weight;
        this.level = level;
    }

    // ID를 포함한 전체 생성자 (매퍼 전용)
    public SkillStatsBySyncJpa(Long id, SyncLevel syncLevel, int basePower, int coinPower,
                               int coinCount, int weight, int level) {
        this.id = id;
        this.syncLevel = syncLevel;
        this.basePower = basePower;
        this.coinPower = coinPower;
        this.coinCount = coinCount;
        this.weight = weight;
        this.level = level;
    }

    /**
     * 도메인 객체로부터 JPA 엔티티 생성
     */
    public static SkillStatsBySyncJpa ofDomain(SkillStatsBySync domain) {
        if (domain == null) {
            return null;
        }
        return new SkillStatsBySyncJpa(
            domain.getId(),
            domain.getSyncLevel(),
            domain.getBasePower(),
            domain.getCoinPower(),
            domain.getCoinCount(),
            domain.getWeight(),
            domain.getLevel()
        );
    }

    /**
     * 도메인 객체로 기존 엔티티 업데이트
     */
    public void updateFromDomain(SkillStatsBySync domain) {
        if (domain == null) {
            return;
        }
        this.syncLevel = domain.getSyncLevel();
        this.basePower = domain.getBasePower();
        this.coinPower = domain.getCoinPower();
        this.coinCount = domain.getCoinCount();
        this.weight = domain.getWeight();
        this.level = domain.getLevel();
    }

    // 양방향 관계 편의 메서드
    public void setSkill(SkillJpa skill) {
        this.skill = skill;
    }

    public void addSkillCoin(SkillCoinJpa coin) {
        this.skillCoins.add(coin);
        coin.setStatsBySync(this);
    }

    public void removeSkillCoin(SkillCoinJpa coin) {
        this.skillCoins.remove(coin);
        coin.setStatsBySync(null);
    }
}
