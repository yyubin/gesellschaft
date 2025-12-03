package dto;

import model.passive.PassiveKind;
import model.passive.PersonaPassive;
import model.skill.SyncLevel;

public record PersonaPassiveResponse(
    Long id,
    PassiveKind kind,
    String name,
    PassiveActivationRequirementResponse activation,
    String originalText,
    SyncLevel syncLevel
) {
    public static PersonaPassiveResponse from(PersonaPassive passive) {
        if (passive == null) {
            return null;
        }

        return new PersonaPassiveResponse(
            passive.getId(),
            passive.getKind(),
            passive.getName(),
            PassiveActivationRequirementResponse.from(passive.getCondition().orElse(null)),
            passive.getEffect() != null ? passive.getEffect().getOriginalText() : null,
            passive.getSyncLevel().orElse(null)
        );
    }
}
