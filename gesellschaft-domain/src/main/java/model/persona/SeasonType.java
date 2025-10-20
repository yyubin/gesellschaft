package model.persona;

import lombok.Getter;

@Getter
public enum SeasonType {
    NORMAL("통상"),
    SEASON_NORMAL("시즌"),
    SEASON_EVENT("시즌 이벤트"),
    WALPURGISNACHT("발푸르기스");

    private final String displayName;

    SeasonType(String displayName) {
        this.displayName = displayName;
    }
}
