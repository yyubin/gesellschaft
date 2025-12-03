package org.yyubin.gesellschaftboot.dto;

import dto.*;
import model.GradeType;

import java.util.List;

public record PersonaResponseDto(
    Long id,
    String name,
    String nameEn,
    SinnerResponseDto prisoner,
    GradeType grade,
    ResistanceInfoResponseDto resistanceInfo,
    SpeedInfoResponseDto speedInfo,
    HealthInfoResponseDto healthInfo,
    SeasonInfoResponseDto seasonInfo,
    int defenseLevel,
    int mentality,
    SubAffiliationResponseDto affiliation,
    List<PersonaPassiveResponseDto> passives,
    List<PersonaImageResponseDto> images,
    List<SkillResponseDto> skills,
    String releaseDate
) {

    public static PersonaResponseDto from(PersonaResponse response) {
        if (response == null) {
            return null;
        }
        return new PersonaResponseDto(
            response.id(),
            response.name(),
            response.nameEn(),
            SinnerResponseDto.from(response.sinner()),
            response.grade(),
            ResistanceInfoResponseDto.from(response.resistanceInfo()),
            SpeedInfoResponseDto.from(response.speedInfo()),
            HealthInfoResponseDto.from(response.healthInfo()),
            SeasonInfoResponseDto.from(response.seasonInfo()),
            response.defenseLevel(),
            response.mentality(),
            SubAffiliationResponseDto.from(response.affiliation()),
            toPassiveResponseDtos(response.passives()),
            toImageResponseDtos(response.images()),
            toSkillResponseDtos(response.skills()),
            response.releaseDate()
        );
    }

    private static List<PersonaPassiveResponseDto> toPassiveResponseDtos(List<PersonaPassiveResponse> passives) {
        if (passives == null) return List.of();
        return passives.stream()
            .map(PersonaPassiveResponseDto::from)
            .toList();
    }

    private static List<PersonaImageResponseDto> toImageResponseDtos(List<PersonaImageResponse> images) {
        if (images == null) return List.of();
        return images.stream()
            .map(PersonaImageResponseDto::from)
            .toList();
    }

    private static List<SkillResponseDto> toSkillResponseDtos(List<SkillResponse> skills) {
        if (skills == null) return List.of();
        return skills.stream()
            .map(SkillResponseDto::from)
            .toList();
    }
}
