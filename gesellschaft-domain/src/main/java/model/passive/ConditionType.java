package model.passive;

import lombok.Getter;

/**
 * 패시브 발동 조건 타입
 */
@Getter
public enum ConditionType {
    HOLD("보유"),       // "오만 보유 3"
    RESONATE("공명");   // "오만 공명 3"

    private final String nameKo;

    ConditionType(String nameKo) {
        this.nameKo = nameKo;
    }
}
