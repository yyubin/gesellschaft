package model.persona;

import lombok.Getter;

@Getter
public class SeasonInfo {
    private final SeasonType seasonType;
    private final Integer number;

    public SeasonInfo(SeasonType seasonType, Integer number) {
        this.seasonType = seasonType;
        this.number = number;
    }

    public boolean isEventSeason() {
        return seasonType == SeasonType.SEASON_EVENT;
    }

    public String getDisplayName() {
        return switch (seasonType) {
            case NORMAL -> "통상";
            case SEASON_NORMAL -> number != null ? number + "시즌" : "시즌";
            case SEASON_EVENT -> number != null ? number + "시즌 (이벤트)" : "이벤트 시즌";
            case WALPURGISNACHT -> number != null ? number + "번째 발푸르기스" : "발푸르기스";
        };
    }
}
