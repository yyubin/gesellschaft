package org.yyubin.gesellschaftinfrastructure.mapper;

import model.skill.Skill;
import model.skill.SkillCoin;
import model.skill.SkillStatsBySync;
import org.springframework.stereotype.Component;
import org.yyubin.gesellschaftinfrastructure.jpa.SkillCoinJpa;
import org.yyubin.gesellschaftinfrastructure.jpa.SkillJpa;
import org.yyubin.gesellschaftinfrastructure.jpa.SkillStatsBySyncJpa;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Skill POJO ↔ SkillJpa 매퍼
 */
@Component
public class SkillMapper {

    // TODO: SkillEffect, CoinEffect 매퍼는 추후 구현
    // private final SkillEffectMapper skillEffectMapper;
    // private final CoinEffectMapper coinEffectMapper;

    /**
     * JPA → POJO 도메인 변환
     */
    public Skill toDomain(SkillJpa jpa) {
        if (jpa == null) {
            return null;
        }

        return Skill.builder()
            .name(jpa.getName())
            .skillNumber(jpa.getSkillNumber())
            .skillCategory(jpa.getSkillCategory())
            .sinAffinity(jpa.getSinAffinity())
            .keywordType(jpa.getKeywordType())
            .skillQuantity(jpa.getSkillQuantity())
            .attackType(jpa.getAttackType())
            .defenseType(jpa.getDefenseType())
            .skillImage(jpa.getSkillImage())
            .statsBySync(
                jpa.getStatsBySync() != null
                    ? jpa.getStatsBySync().stream()
                        .map(this::statsBySyncToDomain)
                        .collect(Collectors.toList())
                    : List.of()
            )
            .build()
            .withId(jpa.getId());
    }

    /**
     * POJO 도메인 → JPA 변환
     */
    public SkillJpa toJpa(Skill domain) {
        if (domain == null) {
            return null;
        }

        SkillJpa jpa = SkillJpa.ofDomain(domain);

        // SkillStatsBySync 변환 및 양방향 관계 설정
        if (domain.getStatsBySync() != null && !domain.getStatsBySync().isEmpty()) {
            domain.getStatsBySync().stream()
                .map(this::statsBySyncToJpa)
                .forEach(jpa::addStatsBySync);
        }

        return jpa;
    }

    /**
     * 기존 JPA 엔티티 업데이트
     */
    public void updateJpaFromDomain(SkillJpa jpa, Skill domain) {
        if (jpa == null || domain == null) {
            return;
        }

        jpa.updateFromDomain(domain);

        // SkillStatsBySync 동기화
        jpa.getStatsBySync().clear();
        if (domain.getStatsBySync() != null) {
            domain.getStatsBySync().stream()
                .map(this::statsBySyncToJpa)
                .forEach(jpa::addStatsBySync);
        }
    }

    // === SkillStatsBySync 변환 ===

    private SkillStatsBySync statsBySyncToDomain(SkillStatsBySyncJpa jpa) {
        if (jpa == null) {
            return null;
        }

        return SkillStatsBySync.builder(jpa.getSyncLevel())
            .basePower(jpa.getBasePower())
            .coinPower(jpa.getCoinPower())
            .coinCount(jpa.getCoinCount())
            .weight(jpa.getWeight())
            .level(jpa.getLevel())
            .skillEffects(
                // TODO: SkillEffect 변환
                List.of()
            )
            .skillCoins(
                jpa.getSkillCoins() != null
                    ? jpa.getSkillCoins().stream()
                        .map(this::skillCoinToDomain)
                        .collect(Collectors.toList())
                    : List.of()
            )
            .build()
            .withId(jpa.getId());
    }

    private SkillStatsBySyncJpa statsBySyncToJpa(SkillStatsBySync domain) {
        if (domain == null) {
            return null;
        }

        SkillStatsBySyncJpa jpa = SkillStatsBySyncJpa.ofDomain(domain);

        // SkillCoin 변환 및 양방향 관계 설정
        if (domain.getSkillCoins() != null && !domain.getSkillCoins().isEmpty()) {
            domain.getSkillCoins().stream()
                .map(this::skillCoinToJpa)
                .forEach(jpa::addSkillCoin);
        }

        // TODO: SkillEffect 변환
        // if (domain.getSkillEffects() != null) {
        //     domain.getSkillEffects().stream()
        //         .map(skillEffectMapper::toJpa)
        //         .forEach(jpa::addSkillEffect);
        // }

        return jpa;
    }

    // === SkillCoin 변환 ===

    private SkillCoin skillCoinToDomain(SkillCoinJpa jpa) {
        if (jpa == null) {
            return null;
        }

        return SkillCoin.builder(jpa.getOrderIndex())
            .coinType(jpa.getCoinType())
            .coinEffects(
                // TODO: CoinEffect 변환
                List.of()
            )
            .build()
            .withId(jpa.getId());
    }

    private SkillCoinJpa skillCoinToJpa(SkillCoin domain) {
        if (domain == null) {
            return null;
        }

        SkillCoinJpa jpa = SkillCoinJpa.ofDomain(domain);

        // TODO: CoinEffect 변환
        // if (domain.getCoinEffects() != null) {
        //     domain.getCoinEffects().stream()
        //         .map(coinEffectMapper::toJpa)
        //         .forEach(jpa::addCoinEffect);
        // }

        return jpa;
    }

    /**
     * 리스트 변환: JPA → 도메인
     */
    public List<Skill> toDomainList(List<SkillJpa> jpaList) {
        if (jpaList == null) {
            return List.of();
        }
        return jpaList.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    /**
     * 리스트 변환: 도메인 → JPA
     */
    public List<SkillJpa> toJpaList(List<Skill> domainList) {
        if (domainList == null) {
            return List.of();
        }
        return domainList.stream()
            .map(this::toJpa)
            .collect(Collectors.toList());
    }
}
