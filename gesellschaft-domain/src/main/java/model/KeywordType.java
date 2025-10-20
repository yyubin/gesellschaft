package model;

import lombok.Getter;

@Getter
public enum KeywordType {
    BURN("화상"),
    BLEED("출혈"),
    TREMOR("진동"),
    RUPTURE("파열"),
    SINKING("침잠"),
    BREATH("호흡"),
    CHARGE("충전"),
    NONE("없음");

    private final String nameKo;

    KeywordType(String nameKo) {
        this.nameKo = nameKo;
    }
}