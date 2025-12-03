package org.yyubin.gesellschaftboot.dto;

import dto.SinnerResponse;

public record SinnerResponseDto(
    Long id,
    String name,
    String nameEn
) {

    public static SinnerResponseDto from(SinnerResponse response) {
        return new SinnerResponseDto(
            response.id(),
            response.name(),
            response.nameEn()
        );
    }
}
