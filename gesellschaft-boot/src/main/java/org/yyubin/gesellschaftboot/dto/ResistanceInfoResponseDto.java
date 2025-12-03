package org.yyubin.gesellschaftboot.dto;

import dto.ResistanceInfoResponse;
import model.persona.ResistanceType;

public record ResistanceInfoResponseDto(
    ResistanceType slashResistance,
    ResistanceType penetrationResistance,
    ResistanceType bluntResistance
) {
    public static ResistanceInfoResponseDto from(ResistanceInfoResponse response) {
        if (response == null) return null;
        return new ResistanceInfoResponseDto(
            response.slashResistance(),
            response.penetrationResistance(),
            response.bluntResistance()
        );
    }
}
