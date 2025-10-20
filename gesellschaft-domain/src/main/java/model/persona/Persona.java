package model.persona;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import model.GradeType;
import model.skill.Skill;

@Getter
public class Persona {

    private final Long id;
    private final String name;
    private final String nameEn;
    private final GradeType grade;
    private final LocalDate releaseDate;
    private final int maxLevel;
    private final ResistanceInfo resistanceInfo;
    private final SpeedInfo speedInfo;
    private final HealthInfo healthInfo;
    private final SeasonInfo seasonInfo;
    private final List<Skill> skills;

    public Persona(Long id, String name, String nameEn, GradeType grade,
                   LocalDate releaseDate, int maxLevel,
                   ResistanceInfo resistanceInfo,
                   SpeedInfo speedInfo,
                   HealthInfo healthInfo,
                   SeasonInfo seasonInfo,
                   List<Skill> skills) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.nameEn = nameEn;
        this.grade = Objects.requireNonNull(grade);
        this.releaseDate = releaseDate;
        this.maxLevel = maxLevel;
        this.resistanceInfo = resistanceInfo;
        this.speedInfo = speedInfo;
        this.healthInfo = healthInfo;
        this.seasonInfo = seasonInfo;
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
    }


    public List<Skill> getSkills() { return Collections.unmodifiableList(skills); }
}
