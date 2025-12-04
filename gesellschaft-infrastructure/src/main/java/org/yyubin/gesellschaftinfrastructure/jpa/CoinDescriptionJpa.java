package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coin_description")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CoinDescriptionJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_coin_id")
    private SkillCoinJpa skillCoin;


    public void setSkillCoin(SkillCoinJpa skillCoin) {
        this.skillCoin = skillCoin;
    }
}
