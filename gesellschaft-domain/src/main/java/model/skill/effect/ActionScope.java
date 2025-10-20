package model.skill.effect;

/**
 * 효과 적용 범위
 */
public enum ActionScope {
    SKILL,          // 스킬 전체
    COINS_SKILL,    // 스킬의 모든 코인
    COIN_EACH,      // 각 코인마다
    COIN_LAST,      // 마지막 코인
    COIN_FIRST,     // 첫 번째 코인
    COIN_INDEX,     // 특정 인덱스 코인
    THIS_COIN,      // 현재 코인 (코인 효과에서)
    NEXT_COIN       // 다음 코인
}
