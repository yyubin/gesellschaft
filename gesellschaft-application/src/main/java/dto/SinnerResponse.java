package dto;

import model.Sinner;

public record SinnerResponse(
    Long id,
    String name,
    String nameEn
) {

    public static SinnerResponse from(Sinner sinner) {
        return new SinnerResponse(
            sinner.getId(),
            sinner.getName(),
            sinner.getNameEn()
        );
    }
}
