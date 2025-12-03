package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * PassiveEffect JPA 엔티티
 * - 인격 패시브 효과 (SkillEffect와 동일한 구조)
 * - trigger → rootCondition → branches → actions 파이프라인
 */
@Entity
@Table(name = "passive_effect")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PassiveEffectJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trigger_json", columnDefinition = "TEXT")
    private String triggerJson;  // JSON 직렬화된 SkillTrigger

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "root_condition_id")
    private ConditionGroupJpa rootCondition;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "passive_effect_id")
    @OrderColumn(name = "branch_order")
    private List<EffectBranchJpa> branches = new ArrayList<>();

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_passive_id")
    private PersonaPassiveJpa personaPassive;

    public PassiveEffectJpa(String triggerJson, ConditionGroupJpa rootCondition, String originalText) {
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

    public void setPersonaPassive(PersonaPassiveJpa personaPassive) {
        this.personaPassive = personaPassive;
    }
}
