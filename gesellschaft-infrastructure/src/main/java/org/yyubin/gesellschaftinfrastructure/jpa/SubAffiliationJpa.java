package org.yyubin.gesellschaftinfrastructure.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.MainAffiliationCategory;
import model.SubAffiliation;

@Entity
@Table(name = "sub_affiliation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SubAffiliationJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "main_category", length = 20)
    private MainAffiliationCategory mainCategory;

    public SubAffiliationJpa(String name, MainAffiliationCategory mainCategory) {
        this.name = name;
        this.mainCategory = mainCategory;
    }

    public SubAffiliationJpa(Long id, String name, MainAffiliationCategory mainCategory) {
        this.id = id;
        this.name = name;
        this.mainCategory = mainCategory;
    }

    public static SubAffiliationJpa ofDomain(SubAffiliation domain) {
        if (domain == null) {
            return null;
        }
        return new SubAffiliationJpa(
            domain.getId(),
            domain.getName(),
            domain.getMainCategory()
        );
    }

    public SubAffiliation toDomain() {
        return new SubAffiliation(this.id, this.name, this.mainCategory);
    }

    public void updateFromDomain(SubAffiliation domain) {
        if (domain == null) {
            return;
        }
        this.name = domain.getName();
        this.mainCategory = domain.getMainCategory();
    }
}
