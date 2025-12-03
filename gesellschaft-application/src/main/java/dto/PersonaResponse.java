package dto;

import model.GradeType;
import model.passive.PersonaPassive;
import model.persona.Persona;
import model.persona.PersonaImage;
import model.skill.Skill;

import java.time.LocalDate;
import java.util.List;


public record PersonaResponse(
    Long id,
    String name,
    String nameEn,
    SinnerResponse sinner,
    GradeType grade,
    ResistanceInfoResponse resistanceInfo,
    SpeedInfoResponse speedInfo,
    HealthInfoResponse healthInfo,
    SeasonInfoResponse seasonInfo,
    int defenseLevel,
    int mentality,
    SubAffiliationResponse affiliation,
    List<PersonaPassiveResponse> passives,
    List<PersonaImageResponse> images,
    List<SkillResponse> skills,
    String releaseDate
) {

    public static PersonaResponse from(Persona persona, SinnerResponse sinner) {
        return new PersonaResponse(
            persona.getId(),
            persona.getName(),
            persona.getNameEn(),
            sinner,
            persona.getGrade(),
            ResistanceInfoResponse.from(persona.getResistanceInfo()),
            SpeedInfoResponse.from(persona.getSpeedInfo()),
            HealthInfoResponse.from(persona.getHealthInfo()),
            SeasonInfoResponse.from(persona.getSeasonInfo()),
            persona.getDefenseLevel(),
            persona.getMentality(),
            SubAffiliationResponse.from(persona.getAffiliation()),
            toPassiveResponses(persona.getPassives()),
            toImageResponses(persona.getImages()),
            toSkillResponses(persona.getSkills(), persona.getId()),
            persona.getReleaseDate() != null ? persona.getReleaseDate().toString() : null
        );
    }

    private static List<PersonaPassiveResponse> toPassiveResponses(List<PersonaPassive> passives) {
        if (passives == null) return List.of();
        return passives.stream()
            .map(PersonaPassiveResponse::from)
            .toList();
    }

    private static List<PersonaImageResponse> toImageResponses(List<PersonaImage> images) {
        if (images == null) return List.of();
        return images.stream()
            .map(PersonaImageResponse::from)
            .toList();
    }

    private static List<SkillResponse> toSkillResponses(List<Skill> skills, Long personaId) {
        if (skills == null) return List.of();
        return skills.stream()
            .map(skill -> SkillResponse.from(skill, personaId))
            .toList();
    }

    private static List<SkillResponse> toSkillResponses(List<Skill> skills) {
        if (skills == null) return List.of();
        return skills.stream()
            .map(SkillResponse::from)
            .toList();
    }
}