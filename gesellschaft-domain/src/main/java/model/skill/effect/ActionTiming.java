package model.skill.effect;

/**
 * 효과 적용 시점
 */
public enum ActionTiming {
    IMMEDIATE,      // 즉시
    THIS_TURN,      // 이번 턴
    NEXT_TURN,      // 다음 턴
    TURN_END,       // 턴 종료 시
    NEXT_COIN,      // 다음 코인
    ATTACK_END      // 공격 종료 시
}
