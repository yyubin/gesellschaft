package model.skill.effect;

/**
 * 대상 선택 전략
 */
public enum SelectorType {
    ALL,            // 모두
    RANDOM,         // 랜덤
    HIGHEST,        // 특정 스탯 최대
    LOWEST,         // 특정 스탯 최소
    FASTEST,        // 속도 가장 빠른
    SLOWEST,        // 속도 가장 느린
    FIRST,          // 첫 번째
    LAST            // 마지막
}
