package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "passive_description")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PassiveDescriptionJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_passive_id")
    private PersonaPassiveJpa personaPassive;

    public void setPersonaPassive(PersonaPassiveJpa personaPassive) {
        this.personaPassive = personaPassive;
    }
}
