package model.skill;

/**
 * 조건 검사 대상
 */
public enum ConditionTarget {
    SELF,           // 자신
    ENEMY,          // 대상 (단일 적)
    ENEMY_ALL,      // 모든 적
    ALLY,           // 아군 (단일)
    ALLY_ALL,       // 모든 아군
    SELF_ALLY,      // 자신 포함 아군
    ANY,            // 모두

    // 조작 패널 위치 기반
    RIGHT_ALLY,     // 우측 아군
    LEFT_ALLY,      // 좌측 아군

    // 특수
    LOWEST_HP_ALLY, // 체력 비율 낮은 아군
    HIGHEST_RESONANCE  // 공명 높은 대상
}
