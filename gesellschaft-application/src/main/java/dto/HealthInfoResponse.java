package dto;

import model.persona.HealthInfo;

public record HealthInfoResponse(
    int baseHealth,
    double growthRate,
    int disturbed1,
    int disturbed2,
    int disturbed3
) {
    public static HealthInfoResponse from(HealthInfo healthInfo) {
        if (healthInfo == null) {
            return null;
        }
        return new HealthInfoResponse(
            healthInfo.getBaseHealth(),
            healthInfo.getGrowthRate(),
            healthInfo.getDisturbed1(),
            healthInfo.getDisturbed2(),
            healthInfo.getDisturbed3()
        );
    }
}
