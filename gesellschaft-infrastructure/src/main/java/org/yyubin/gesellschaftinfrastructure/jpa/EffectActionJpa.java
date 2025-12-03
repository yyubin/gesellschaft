package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.skill.effect.*;

/**
 * EffectAction JPA 엔티티
 * - 효과의 실제 동작을 정의 (상태 부여, 버프, 위력 증가 등)
 */
@Entity
@Table(name = "effect_action")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EffectActionJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private ActionType actionType;

    @Column(name = "stat_code")
    private String statCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope")
    private ActionScope scope;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy")
    private ApplyPolicy policy;

    @Column(name = "amount_json", columnDefinition = "TEXT")
    private String amountJson;  // JSON 직렬화된 AmountExpression

    @Enumerated(EnumType.STRING)
    @Column(name = "timing")
    private ActionTiming timing;

    @Column(name = "duration_turns")
    private Integer durationTurns;

    @Column(name = "cap_max")
    private Integer capMax;

    @Column(name = "cap_per_target")
    private Integer capPerTarget;

    @Column(name = "priority")
    private int priority;

    @Column(name = "target_selector_json", columnDefinition = "TEXT")
    private String targetSelectorJson;  // JSON 직렬화된 TargetSelector

    @Column(name = "coin_selector_json", columnDefinition = "TEXT")
    private String coinSelectorJson;  // JSON 직렬화된 CoinSelector

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "effect_branch_id")
    private EffectBranchJpa effectBranch;

    public EffectActionJpa(ActionType actionType, String statCode, ActionScope scope,
                           ApplyPolicy policy, String amountJson, ActionTiming timing,
                           Integer durationTurns, Integer capMax, Integer capPerTarget,
                           int priority, String targetSelectorJson, String coinSelectorJson) {
        this.actionType = actionType;
        this.statCode = statCode;
        this.scope = scope;
        this.policy = policy;
        this.amountJson = amountJson;
        this.timing = timing;
        this.durationTurns = durationTurns;
        this.capMax = capMax;
        this.capPerTarget = capPerTarget;
        this.priority = priority;
        this.targetSelectorJson = targetSelectorJson;
        this.coinSelectorJson = coinSelectorJson;
    }

    public void setEffectBranch(EffectBranchJpa effectBranch) {
        this.effectBranch = effectBranch;
    }
}
