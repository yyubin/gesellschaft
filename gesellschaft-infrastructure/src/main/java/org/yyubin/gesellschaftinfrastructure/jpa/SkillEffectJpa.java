package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * SkillEffect JPA 엔티티
 * - 스킬 전체에 적용되는 효과 (예: [사용시] 충전 10 소모)
 * - trigger → rootCondition → branches → actions 파이프라인
 */
@Entity
@Table(name = "skill_effect")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SkillEffectJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trigger_json", columnDefinition = "TEXT", nullable = false)
    private String triggerJson;  // JSON 직렬화된 SkillTrigger

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "root_condition_id")
    private ConditionGroupJpa rootCondition;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "skill_effect_id")
    @OrderColumn(name = "branch_order")
    private List<EffectBranchJpa> branches = new ArrayList<>();

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stats_by_sync_id")
    private SkillStatsBySyncJpa statsBySync;

    public SkillEffectJpa(String triggerJson, ConditionGroupJpa rootCondition, String originalText) {
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

    public void setStatsBySync(SkillStatsBySyncJpa statsBySync) {
        this.statsBySync = statsBySync;
    }
}
