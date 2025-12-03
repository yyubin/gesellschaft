package org.yyubin.gesellschaftboot.dto;

import dto.PassiveActivationRequirementResponse;
import model.SinAffinity;
import model.passive.ConditionType;

public record PassiveActivationRequirementResponseDto(
    SinAffinity attribute,
    ConditionType invocationType,
    int count
) {
    public static PassiveActivationRequirementResponseDto from(PassiveActivationRequirementResponse response) {
        if (response == null) return null;
        return new PassiveActivationRequirementResponseDto(
            response.attribute(),
            response.invocationType(),
            response.count()
        );
    }
}
