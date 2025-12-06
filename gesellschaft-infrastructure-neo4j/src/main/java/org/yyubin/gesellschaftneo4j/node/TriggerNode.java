package org.yyubin.gesellschaftneo4j.node;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Trigger")
@Getter
@Setter
@NoArgsConstructor
public class TriggerNode {

    @Id
    @GeneratedValue
    private Long id;

    private String type;  // ON_HIT, ON_HEAD_HIT, ON_CLASH, ON_WIN_CLASH, etc
    private String name;  // Korean name (적중시, 앞면 적중시, 합 진행시, 합 승리시, etc)

    @Builder
    public TriggerNode(Long id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }
}
