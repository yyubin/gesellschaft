package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.skill.CoinType;
import model.skill.SkillCoin;

import java.util.ArrayList;
import java.util.List;

/**
 * SkillCoin JPA 엔티티
 * - 스킬 코인별 효과 정의
 */
@Entity
@Table(name = "skill_coin")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SkillCoinJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int orderIndex;  // 코인 순서 (0부터)

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CoinType coinType;

    // 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stats_by_sync_id")
    private SkillStatsBySyncJpa statsBySync;

    @OneToMany(mappedBy = "skillCoin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillDescriptionJpa> skillEffects = new ArrayList<>();

    @Builder
    public SkillCoinJpa(int orderIndex, CoinType coinType) {
        this.orderIndex = orderIndex;
        this.coinType = coinType;
    }

    // ID를 포함한 전체 생성자 (매퍼 전용)
    public SkillCoinJpa(Long id, int orderIndex, CoinType coinType) {
        this.id = id;
        this.orderIndex = orderIndex;
        this.coinType = coinType;
    }

    /**
     * 도메인 객체로부터 JPA 엔티티 생성
     */
    public static SkillCoinJpa ofDomain(SkillCoin domain) {
        if (domain == null) {
            return null;
        }
        return new SkillCoinJpa(
            domain.getId(),
            domain.getOrderIndex(),
            domain.getCoinType()
        );
    }

    /**
     * 도메인 객체로 기존 엔티티 업데이트
     */
    public void updateFromDomain(SkillCoin domain) {
        if (domain == null) {
            return;
        }
        this.orderIndex = domain.getOrderIndex();
        this.coinType = domain.getCoinType();
    }

    // 양방향 관계 편의 메서드
    public void setStatsBySync(SkillStatsBySyncJpa statsBySync) {
        this.statsBySync = statsBySync;
    }
}
