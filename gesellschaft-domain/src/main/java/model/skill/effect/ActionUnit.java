package model.skill.effect;

/**
 * 수치 단위
 */
public enum ActionUnit {
    STACK,              // 스택 (상태 이상)
    FLAT,               // 절대값
    PERCENT,            // 백분율 (%)
    PERCENT_OF_MAX_HP,  // 최대 체력의 %
    PERCENT_OF_DAMAGE,  // 입힌 피해의 %
    PER_N,              // N당 (설정값 필요)
    PER_3,              // 3당
    PER_10,             // 10당
    UNITLESS            // 단위 없음
}
