package dto;

import model.SinAffinity;
import model.passive.ConditionType;
import model.passive.PassiveCondition;

public record PassiveActivationRequirementResponse(
    SinAffinity attribute,
    ConditionType invocationType,
    int count
) {
    public static PassiveActivationRequirementResponse from(PassiveCondition condition) {
        if (condition == null) {
            return null;
        }
        return new PassiveActivationRequirementResponse(
            condition.getSinAffinity(),
            condition.getConditionType(),
            condition.getCount()
        );
    }
}
