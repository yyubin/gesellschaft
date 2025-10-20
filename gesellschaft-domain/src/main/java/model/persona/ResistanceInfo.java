package model.persona;

import java.util.Objects;
import lombok.Getter;
import model.AttackType;

@Getter
public class ResistanceInfo {

    private final ResistanceType slashResistance;       // 참격 내성
    private final ResistanceType penetrationResistance; // 관통 내성
    private final ResistanceType bluntResistance;       // 타격 내성

    public ResistanceInfo(ResistanceType slashResistance,
                          ResistanceType penetrationResistance,
                          ResistanceType bluntResistance) {
        this.slashResistance = Objects.requireNonNull(slashResistance);
        this.penetrationResistance = Objects.requireNonNull(penetrationResistance);
        this.bluntResistance = Objects.requireNonNull(bluntResistance);
    }

    public ResistanceType getResistanceByType(AttackType type) {
        return switch (type) {
            case SLASH -> slashResistance;
            case PIERCE -> penetrationResistance;
            case BLUNT -> bluntResistance;
        };
    }

    // 피해량 보정치를 반환 (예: WEAK → 1.5배, NORMAL → 1.0배, RESIST → 0.5배)
    public double getDamageMultiplier(AttackType type) {
        return switch (getResistanceByType(type)) {
            case WEAK -> 1.5;
            case NORMAL -> 1.0;
            case RESIST -> 0.5;
        };
    }

}
