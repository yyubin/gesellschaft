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

@Node("Persona")
@Getter
@Setter
@NoArgsConstructor
public class PersonaNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String nameEn;

    // Basic info
    private String grade;
    private String releaseDate;
    private Integer defenseLevel;

    // Season info
    private String seasonType;
    private Integer seasonNumber;

    // Resistance
    private String resSlash;
    private String resPierce;
    private String resBlunt;

    // Speed
    private Integer minSpeed;
    private Integer maxSpeed;

    // Health
    private Integer baseHealth;
    private Double growthRate;
    private List<Integer> disturbedLevels = new ArrayList<>();

    // Relationships
    @Relationship(type = "HAS_SKILL", direction = Relationship.Direction.OUTGOING)
    private List<SkillNode> skills = new ArrayList<>();

    @Relationship(type = "HAS_PASSIVE", direction = Relationship.Direction.OUTGOING)
    private List<PassiveNode> passives = new ArrayList<>();

    @Builder
    public PersonaNode(Long id, String name, String nameEn, String grade, String releaseDate,
                       Integer defenseLevel, String seasonType, Integer seasonNumber,
                       String resSlash, String resPierce, String resBlunt,
                       Integer minSpeed, Integer maxSpeed,
                       Integer baseHealth, Double growthRate, List<Integer> disturbedLevels,
                       List<SkillNode> skills, List<PassiveNode> passives) {
        this.id = id;
        this.name = name;
        this.nameEn = nameEn;
        this.grade = grade;
        this.releaseDate = releaseDate;
        this.defenseLevel = defenseLevel;
        this.seasonType = seasonType;
        this.seasonNumber = seasonNumber;
        this.resSlash = resSlash;
        this.resPierce = resPierce;
        this.resBlunt = resBlunt;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.baseHealth = baseHealth;
        this.growthRate = growthRate;
        this.disturbedLevels = disturbedLevels != null ? disturbedLevels : new ArrayList<>();
        this.skills = skills != null ? skills : new ArrayList<>();
        this.passives = passives != null ? passives : new ArrayList<>();
    }
}
