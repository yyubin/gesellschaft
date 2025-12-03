package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.skill.ConditionOperator;
import model.skill.ConditionScope;
import model.skill.ConditionTarget;

/**
 * StatCondition JPA 엔티티
 * - AbstractCondition을 상속하는 리프 조건
 * - 특정 스탯에 대한 조건 검사 (예: SELF.CHARGE >= 5)
 */
@Entity
@Table(name = "stat_condition")
@DiscriminatorValue("STAT")
@PrimaryKeyJoinColumn(name = "id", foreignKey = @ForeignKey(name = "fk_stat_condition_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StatConditionJpa extends AbstractConditionJpa {

    @Enumerated(EnumType.STRING)
    @Column(name = "target")
    private ConditionTarget target;

    @Column(name = "stat_code")
    private String statCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator")
    private ConditionOperator operator;

    @Column(name = "threshold")
    private int threshold;

    public StatConditionJpa(ConditionScope scope, ConditionTarget target, String statCode,
                            ConditionOperator operator, int threshold) {
        super(scope);
        this.target = target;
        this.statCode = statCode;
        this.operator = operator;
        this.threshold = threshold;
    }
}
