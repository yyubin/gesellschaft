package org.yyubin.gesellschaftneo4j.node;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Action")
@Getter
@Setter
@NoArgsConstructor
public class ActionNode {

    @Id
    @GeneratedValue
    private Long id;

    private String type;          // APPLY, MODIFY
    private String target;        // TARGET, ALLY, SELF
    private String stat;          // RUPTURE, PARALYSIS, CLASH_POWER_UP, etc
    private String modifyTarget;  // POWER (for MODIFY type)
    private String value;         // +1, +2, -2, etc
    private Boolean nextTurn;     // Effect applies next turn
    private String coinSelector;  // LAST (for MODIFY type)

    @Relationship(type = "APPLIES", direction = Relationship.Direction.OUTGOING)
    private StatusEffectNode appliesStatus;

    @Builder
    public ActionNode(Long id, String type, String target, String stat, String modifyTarget,
                      String value, Boolean nextTurn, String coinSelector, StatusEffectNode appliesStatus) {
        this.id = id;
        this.type = type;
        this.target = target;
        this.stat = stat;
        this.modifyTarget = modifyTarget;
        this.value = value;
        this.nextTurn = nextTurn;
        this.coinSelector = coinSelector;
        this.appliesStatus = appliesStatus;
    }
}
