package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.skill.ConditionScope;
import model.skill.LogicalOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * ConditionGroup JPA 엔티티
 * - AbstractCondition을 상속하는 복합 조건 (AND/OR)
 * - children으로 AbstractCondition을 재귀적으로 참조
 */
@Entity
@Table(name = "condition_group")
@DiscriminatorValue("GROUP")
@PrimaryKeyJoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_condition_group_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ConditionGroupJpa extends AbstractConditionJpa {

    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false)
    private LogicalOperator operator;

    @Column(name = "ordered", nullable = false)
    private boolean ordered;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "parent_group_id")
    @OrderColumn(name = "child_order")
    private List<AbstractConditionJpa> children = new ArrayList<>();

    public ConditionGroupJpa(ConditionScope scope, LogicalOperator operator, boolean ordered) {
        super(scope);
        this.operator = operator;
        this.ordered = ordered;
    }

    public void addChild(AbstractConditionJpa child) {
        this.children.add(child);
    }

    public void setChildren(List<AbstractConditionJpa> children) {
        this.children = children;
    }
}
