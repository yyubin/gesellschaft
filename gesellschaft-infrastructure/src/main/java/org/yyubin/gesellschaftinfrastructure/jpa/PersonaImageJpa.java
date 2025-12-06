package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.persona.ImageType;
import model.persona.PersonaImage;

/**
 * PersonaImage JPA 엔티티
 * - 인격 이미지
 */
@Entity
@Table(name = "persona_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PersonaImageJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10)
    private ImageType type;

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "priority")
    private int priority;

    @Column(name = "is_primary")
    private boolean isPrimary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private PersonaJpa persona;

    @Builder
    public PersonaImageJpa(ImageType type, String url, int priority, boolean isPrimary) {
        this.type = type;
        this.url = url;
        this.priority = priority;
        this.isPrimary = isPrimary;
    }

    // ID를 포함한 전체 생성자 (매퍼 전용)
    public PersonaImageJpa(Long id, ImageType type, String url, int priority, boolean isPrimary) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.priority = priority;
        this.isPrimary = isPrimary;
    }

    /**
     * 도메인 객체로부터 JPA 엔티티 생성
     */
    public static PersonaImageJpa ofDomain(PersonaImage domain) {
        if (domain == null) {
            return null;
        }
        return new PersonaImageJpa(
            domain.getId(),
            domain.getType(),
            domain.getUrl(),
            domain.getPriority(),
            domain.isPrimary()
        );
    }

    /**
     * 도메인 객체로 기존 엔티티 업데이트
     */
    public void updateFromDomain(PersonaImage domain) {
        if (domain == null) {
            return;
        }
        this.type = domain.getType();
        this.url = domain.getUrl();
        this.priority = domain.getPriority();
        this.isPrimary = domain.isPrimary();
    }

    // 양방향 관계 설정
    public void setPersona(PersonaJpa persona) {
        this.persona = persona;
    }
}
