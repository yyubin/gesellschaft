package model.skill.effect;

/**
 * 효과 동작 타입
 */
public enum ActionType {
    // 상태 이상
    STATUS_INFLICT,     // 상태 이상 부여
    STATUS_REMOVE,      // 상태 해제

    // 스탯 버프/디버프
    BUFF_DAMAGE_UP,     // 공격력 증가
    BUFF_DAMAGE_DOWN,   // 공격력 감소
    BUFF_DEFENSE_UP,    // 방어력 증가
    BUFF_DEFENSE_DOWN,  // 방어력 감소

    // 리소스
    RESOURCE_GAIN,      // 자원 획득 (충전, 경혈 등)
    RESOURCE_CONSUME,   // 자원 소모
    RESOURCE_SET,       // 자원 설정 (고정값으로)

    // 피해/회복
    DAMAGE_MODIFY,      // 피해량 변경 (%)
    POWER_MODIFY,       // 위력 변경
    HEAL_HP,            // 체력 회복
    CONSUME_HP,         // 체력 소모

    // 코인
    COIN_POWER_UP,      // 코인 위력 +
    CLASH_POWER_UP,     // 합 위력 +

    // 특수
    COMMAND_ATTACK,     // 원호 공격 명령
    TRANSFORM_SKILL,    // 스킬 변환
    SUPPRESS_EFFECT,    // 기존 효과 억제

    ETC                 // 기타
}
