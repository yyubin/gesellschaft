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

    @Column(unique = true, nullable = false, length = 20)
    private String name;

    @Column(unique = true, nullable = false, length = 40)
    private String nameEn;

    @OneToMany(mappedBy = "sinner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaJpa> personas = new ArrayList<>();

    @Builder
    public SinnerJpa(String name, String nameEn) {
        this.name = name;
        this.nameEn = nameEn;
    }
}