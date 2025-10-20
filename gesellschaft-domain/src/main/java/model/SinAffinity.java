package model;

import lombok.Getter;

@Getter
public enum SinAffinity {
    WRATH("분노"),
    LUST("색욕"),
    SLOTH("나태"),
    GREED("탐식"),
    GLOOM("우울"),
    PRIDE("오만"),
    ENVY("질투"),
    NONE("없음");

    private final String nameKo;

    SinAffinity(String nameKo) {
        this.nameKo = nameKo;
    }
}