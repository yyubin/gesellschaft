package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * CoinEffect JPA 엔티티
 * - 개별 코인에 적용되는 효과 (예: [적중시] 출혈 2 부여)
 * - SkillEffect와 동일한 구조 (trigger → rootCondition → branches → actions)
 */
@Entity
@Table(name = "coin_effect")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CoinEffectJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trigger_json", columnDefinition = "TEXT", nullable = false)
    private String triggerJson;  // JSON 직렬화된 SkillTrigger

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "root_condition_id")
    private ConditionGroupJpa rootCondition;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "coin_effect_id")
    @OrderColumn(name = "branch_order")
    private List<EffectBranchJpa> branches = new ArrayList<>();

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_coin_id")
    private SkillCoinJpa skillCoin;

    public CoinEffectJpa(String triggerJson, ConditionGroupJpa rootCondition, String originalText) {
        this.triggerJson = triggerJson;
        this.rootCondition = rootCondition;
        this.originalText = originalText;
    }

    public void addBranch(EffectBranchJpa branch) {
        this.branches.add(branch);
    }

    public void setBranches(List<EffectBranchJpa> branches) {
        this.branches = branches;
    }

    public void setSkillCoin(SkillCoinJpa skillCoin) {
        this.skillCoin = skillCoin;
    }
}
