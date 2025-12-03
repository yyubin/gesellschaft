package dto;

import model.persona.SeasonInfo;
import model.persona.SeasonType;

public record SeasonInfoResponse(
    SeasonType seasonType,
    Integer number
) {
    public static SeasonInfoResponse from(SeasonInfo seasonInfo) {
        if (seasonInfo == null) {
            return null;
        }
        return new SeasonInfoResponse(
            seasonInfo.getSeasonType(),
            seasonInfo.getNumber()
        );
    }
}
