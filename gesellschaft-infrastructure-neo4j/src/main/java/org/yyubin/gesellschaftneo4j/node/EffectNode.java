package org.yyubin.gesellschaftneo4j.node;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Effect")
@Getter
@Setter
@NoArgsConstructor
public class EffectNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String trigger;  // ON_HIT, ON_HEAD_HIT, ON_CLASH, ON_WIN_CLASH, etc

    @Relationship(type = "TRIGGERS_ON", direction = Relationship.Direction.OUTGOING)
    private TriggerNode triggerNode;

    @Relationship(type = "PERFORMS", direction = Relationship.Direction.OUTGOING)
    private List<ActionNode> actions = new ArrayList<>();

    @Builder
    public EffectNode(Long id, String name, String trigger, TriggerNode triggerNode, List<ActionNode> actions) {
        this.id = id;
        this.name = name;
        this.trigger = trigger;
        this.triggerNode = triggerNode;
        this.actions = actions != null ? actions : new ArrayList<>();
    }
}
