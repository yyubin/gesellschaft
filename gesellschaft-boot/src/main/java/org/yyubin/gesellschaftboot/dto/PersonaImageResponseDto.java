package org.yyubin.gesellschaftboot.dto;

import dto.PersonaImageResponse;
import model.persona.ImageType;

public record PersonaImageResponseDto(
    ImageType type,
    String url,
    int priority,
    boolean primary
) {
    public static PersonaImageResponseDto from(PersonaImageResponse response) {
        if (response == null) return null;
        return new PersonaImageResponseDto(
            response.type(),
            response.url(),
            response.priority(),
            response.primary()
        );
    }
}
