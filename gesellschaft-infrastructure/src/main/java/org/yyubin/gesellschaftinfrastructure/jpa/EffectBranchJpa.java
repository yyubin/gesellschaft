package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * EffectBranch JPA 엔티티
 * - 조건부 효과 분기를 정의
 * - condition이 충족되면 actions를 실행
 * - stopOnMatch가 true면 다음 브랜치 스킵 ("대신" 효과)
 */
@Entity
@Table(name = "effect_branch")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class EffectBranchJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "condition_id")
    private ConditionGroupJpa condition;

    @OneToMany(mappedBy = "effectBranch", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "action_order")
    private List<EffectActionJpa> actions = new ArrayList<>();

    @Column(name = "branch_order", nullable = false)
    private int order;

    @Column(name = "stop_on_match", nullable = false)
    private boolean stopOnMatch;

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    public EffectBranchJpa(ConditionGroupJpa condition, int order, boolean stopOnMatch, String originalText) {
        this.condition = condition;
        this.order = order;
        this.stopOnMatch = stopOnMatch;
        this.originalText = originalText;
    }

    public void addAction(EffectActionJpa action) {
        action.setEffectBranch(this);
        this.actions.add(action);
    }

    public void setActions(List<EffectActionJpa> actions) {
        this.actions.clear();
        actions.forEach(this::addAction);
    }
}
