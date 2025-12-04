package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "skill_description")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SkillDescriptionJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stats_by_sync_id")
    private SkillStatsBySyncJpa statsBySync;


    public void setStatsBySync(SkillStatsBySyncJpa statsBySync) {
        this.statsBySync = statsBySync;
    }
}
