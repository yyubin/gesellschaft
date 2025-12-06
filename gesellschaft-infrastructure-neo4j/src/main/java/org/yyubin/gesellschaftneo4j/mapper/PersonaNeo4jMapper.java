package org.yyubin.gesellschaftneo4j.mapper;

import model.GradeType;
import model.persona.*;
import org.springframework.stereotype.Component;
import org.yyubin.gesellschaftneo4j.node.PersonaNode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PersonaNeo4jMapper {

    public Persona toDomain(PersonaNode node) {
        if (node == null) {
            return null;
        }

        // Value Objects 재구성
        ResistanceInfo resistanceInfo = new ResistanceInfo(
            parseResistanceType(node.getResSlash()),
            parseResistanceType(node.getResPierce()),
            parseResistanceType(node.getResBlunt())
        );

        SpeedInfo speedInfo = new SpeedInfo(
            node.getMinSpeed() != null ? node.getMinSpeed() : 0,
            node.getMaxSpeed() != null ? node.getMaxSpeed() : 0
        );

        HealthInfo healthInfo = new HealthInfo(
            node.getBaseHealth() != null ? node.getBaseHealth() : 0,
            node.getGrowthRate() != null ? node.getGrowthRate() : 0.0,
            0, 0, 0 // disturbed levels - Neo4j에서는 간소화
        );

        SeasonInfo seasonInfo = node.getSeasonType() != null
            ? new SeasonInfo(
                parseSeasonType(node.getSeasonType()),
                node.getSeasonNumber()
            )
            : null;

        LocalDate releaseDate = node.getReleaseDate() != null
            ? LocalDate.parse(node.getReleaseDate(), DateTimeFormatter.ISO_DATE)
            : null;

        // 기본 Persona 생성 (스킬, 패시브, 이미지는 빈 리스트)
        return Persona.create(
            node.getName(),
            node.getNameEn(),
            parseGradeType(node.getGrade()),
            releaseDate,
            55, // 기본 최대 레벨
            resistanceInfo,
            speedInfo,
            healthInfo,
            seasonInfo,
            node.getDefenseLevel() != null ? node.getDefenseLevel() : 0,
            0, // mentality - Neo4j에서는 간소화
            null, // affiliation - Neo4j에서는 간소화
            List.of(), // skills
            List.of(), // passives
            List.of()  // images
        ).withId(node.getId());
    }

    public List<Persona> toDomainList(List<PersonaNode> nodes) {
        return nodes.stream()
            .map(this::toDomain)
            .toList();
    }

    private GradeType parseGradeType(String grade) {
        if (grade == null) return GradeType.ONE;
        return switch (grade.toUpperCase()) {
            case "ONE", "1" -> GradeType.ONE;
            case "TWO", "2" -> GradeType.TWO;
            case "THREE", "3" -> GradeType.THREE;
            default -> GradeType.ONE;
        };
    }

    private ResistanceType parseResistanceType(String resistance) {
        if (resistance == null) return ResistanceType.NORMAL;
        return switch (resistance.toUpperCase()) {
            case "WEAK" -> ResistanceType.WEAK;
            case "RESIST" -> ResistanceType.RESIST;
            default -> ResistanceType.NORMAL;
        };
    }

    private SeasonType parseSeasonType(String season) {
        if (season == null) return SeasonType.NORMAL;
        return switch (season.toUpperCase()) {
            case "SEASON_NORMAL" -> SeasonType.SEASON_NORMAL;
            case "SEASON_EVENT" -> SeasonType.SEASON_EVENT;
            case "WALPURGISNACHT" -> SeasonType.WALPURGISNACHT;
            default -> SeasonType.NORMAL;
        };
    }
}
