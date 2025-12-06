package org.yyubin.gesellschaftinfrastructure.jpa;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sinner")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SinnerJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 20)
    private String name;

    @Column(name = "name_en", unique = true, nullable = false, length = 40)
    private String nameEn;

    @OneToMany(mappedBy = "sinner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaJpa> personas = new ArrayList<>();

    @Builder
    public SinnerJpa(String name, String nameEn) {
        this.name = name;
        this.nameEn = nameEn;
    }

    // ID를 포함한 전체 생성자 (매퍼 전용)
    public SinnerJpa(Long id, String name, String nameEn) {
        this.id = id;
        this.name = name;
        this.nameEn = nameEn;
    }

    /**
     * 도메인 객체로부터 JPA 엔티티 생성
     * @param domain Sinner 도메인 객체
     * @return SinnerJpa 엔티티
     */
    public static SinnerJpa ofDomain(model.Sinner domain) {
        if (domain == null) {
            return null;
        }
        return new SinnerJpa(domain.getId(), domain.getName(), domain.getNameEn());
    }

    /**
     * 도메인 객체로 기존 엔티티 업데이트
     * @param domain Sinner 도메인 객체
     */
    public void updateFromDomain(model.Sinner domain) {
        if (domain == null) {
            return;
        }
        this.name = domain.getName();
        this.nameEn = domain.getNameEn();
    }

    /**
     * Persona 추가 (양방향 관계 동기화)
     * @param persona PersonaJpa 엔티티
     */
    public void addPersona(PersonaJpa persona) {
        this.personas.add(persona);
        persona.setSinner(this);
    }

    /**
     * Persona 제거 (양방향 관계 동기화)
     * @param persona PersonaJpa 엔티티
     */
    public void removePersona(PersonaJpa persona) {
        this.personas.remove(persona);
        persona.setSinner(null);
    }
}