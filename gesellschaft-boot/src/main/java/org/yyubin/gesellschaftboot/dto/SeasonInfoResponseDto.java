package org.yyubin.gesellschaftboot.dto;

import dto.SeasonInfoResponse;
import model.persona.SeasonType;

public record SeasonInfoResponseDto(
    SeasonType seasonType,
    Integer number
) {
    public static SeasonInfoResponseDto from(SeasonInfoResponse response) {
        if (response == null) return null;
        return new SeasonInfoResponseDto(
            response.seasonType(),
            response.number()
        );
    }
}
