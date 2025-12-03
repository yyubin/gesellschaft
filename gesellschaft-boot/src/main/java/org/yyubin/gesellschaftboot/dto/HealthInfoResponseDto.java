package org.yyubin.gesellschaftboot.dto;

import dto.HealthInfoResponse;

public record HealthInfoResponseDto(
    int baseHealth,
    double growthRate,
    int disturbed1,
    int disturbed2,
    int disturbed3
) {
    public static HealthInfoResponseDto from(HealthInfoResponse response) {
        if (response == null) return null;
        return new HealthInfoResponseDto(
            response.baseHealth(),
            response.growthRate(),
            response.disturbed1(),
            response.disturbed2(),
            response.disturbed3()
        );
    }
}
