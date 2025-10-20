package model.skill;

public enum TriggerCode {
    NONE,

    // 스킬 트리거
    ON_HIT,                   // [적중시]
    ON_CRITICAL_HIT,          // [크리티컬 적중시]
    ON_HEAD_HIT,              // [앞면 적중시], [앞면 적중 시]
    ON_TAIL_HIT,              // [뒷면 적중시], [뒷면 적중 시]
    ON_USE,                   // [사용시]
    ON_WIN_CLASH,             // [합 승리시], [합 승리 시]
    ON_WIN_CLASH_HIT,         // [합 승리 적중시], [합 승리 적중 시]
    ON_LOSE_CLASH,            // [합 패배시], [합 패배 시]
    ON_DROP,                  // [이 스킬이 버려지면]
    ON_ATTACK_END,            // [공격 종료 시]
    ON_KILL,                  // [적 처치 시]

    // 패시브 트리거
    ON_BATTLE_START,          // [전투 시작 시]
    ON_TURN_START,            // [턴 시작 시]
    ON_TURN_END,              // [턴 종료 시]
    ON_ALLY_ATTACK,           // [아군이 공격을 가할 시]
    ON_ALLY_HIT,              // [아군이 적중 시]
    ON_ALLY_KILL,             // [아군이 적 처치 시]
    ON_DAMAGED,               // [피격 시]
    ON_STATUS_INFLICTED,      // [상태 이상 부여 시]
    ON_STATUS_RECEIVED,       // [상태 이상 받을 시]
    ALWAYS,                   // [전투 중] (항상 활성)

    // 기타
    ON_CONDITION_MET          // 예외 조건: 명시된 조건 충족 시
}
