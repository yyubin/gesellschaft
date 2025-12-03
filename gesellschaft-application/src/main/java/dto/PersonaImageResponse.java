package dto;

import model.persona.ImageType;
import model.persona.PersonaImage;

public record PersonaImageResponse(
    ImageType type,
    String url,
    int priority,
    boolean primary
) {
    public static PersonaImageResponse from(PersonaImage image) {
        if (image == null) {
            return null;
        }
        return new PersonaImageResponse(
            image.getType(),
            image.getUrl(),
            image.getPriority(),
            image.isPrimary()
        );
    }
}
