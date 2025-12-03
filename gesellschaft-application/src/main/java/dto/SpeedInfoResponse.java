package dto;

import model.persona.SpeedInfo;

public record SpeedInfoResponse(
    int minSpeed,
    int maxSpeed
) {
    public static SpeedInfoResponse from(SpeedInfo speedInfo) {
        if (speedInfo == null) {
            return null;
        }
        return new SpeedInfoResponse(
            speedInfo.getMinSpeed(),
            speedInfo.getMaxSpeed()
        );
    }
}
