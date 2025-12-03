package org.yyubin.gesellschaftboot.dto;

import dto.SpeedInfoResponse;

public record SpeedInfoResponseDto(
    int minSpeed,
    int maxSpeed
) {
    public static SpeedInfoResponseDto from(SpeedInfoResponse response) {
        if (response == null) return null;
        return new SpeedInfoResponseDto(
            response.minSpeed(),
            response.maxSpeed()
        );
    }
}
