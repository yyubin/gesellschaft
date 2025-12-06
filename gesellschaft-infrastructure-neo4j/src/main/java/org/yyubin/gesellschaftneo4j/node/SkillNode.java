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

@Node("Skill")
@Getter
@Setter
@NoArgsConstructor
public class SkillNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Integer skillNumber;

    // Skill properties
    private String category;       // ATTACK, DEFENSE
    private String sin;            // SinAffinity
    private String attackType;     // SLASH, PIERCE, BLUNT
    private String defenseType;    // GUARD, EVADE, COUNTER
    private String keyword;        // StatusEffect keyword

    // Sync stats
    private String syncLevel;      // SYNC_1, SYNC_2, SYNC_3, SYNC_4
    private Integer basePower;
    private Integer coinPower;
    private Integer coinCount;
    private Integer weight;
    private Integer level;

    // Relationships
    @Relationship(type = "HAS_COIN", direction = Relationship.Direction.OUTGOING)
    private List<CoinNode> coins = new ArrayList<>();

    @Relationship(type = "HAS_EFFECT", direction = Relationship.Direction.OUTGOING)
    private List<EffectNode> effects = new ArrayList<>();

    @Relationship(type = "USES_SIN", direction = Relationship.Direction.OUTGOING)
    private SinAffinityNode sinAffinity;

    @Relationship(type = "HAS_KEYWORD", direction = Relationship.Direction.OUTGOING)
    private StatusEffectNode keywordStatus;

    @Builder
    public SkillNode(Long id, String name, Integer skillNumber, String category, String sin,
                     String attackType, String defenseType, String keyword,
                     String syncLevel, Integer basePower, Integer coinPower, Integer coinCount,
                     Integer weight, Integer level,
                     List<CoinNode> coins, List<EffectNode> effects,
                     SinAffinityNode sinAffinity, StatusEffectNode keywordStatus) {
        this.id = id;
        this.name = name;
        this.skillNumber = skillNumber;
        this.category = category;
        this.sin = sin;
        this.attackType = attackType;
        this.defenseType = defenseType;
        this.keyword = keyword;
        this.syncLevel = syncLevel;
        this.basePower = basePower;
        this.coinPower = coinPower;
        this.coinCount = coinCount;
        this.weight = weight;
        this.level = level;
        this.coins = coins != null ? coins : new ArrayList<>();
        this.effects = effects != null ? effects : new ArrayList<>();
        this.sinAffinity = sinAffinity;
        this.keywordStatus = keywordStatus;
    }
}
