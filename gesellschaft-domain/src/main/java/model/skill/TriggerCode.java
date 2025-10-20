package model.skill;

public enum TriggerCode {
    NONE,
    ON_HIT,                   // [적중시]
    ON_CRITICAL_HIT,          // [크리티컬 적중시]
    ON_HEAD_HIT,              // [앞면 적중시], [앞면 적중 시]
    ON_TAIL_HIT,              // [뒷면 적중시], [뒷면 적중 시]
    ON_USE,                   // [사용시]
    ON_BATTLE_BEFORE,         // [전투 시작 전]
    ON_BATTLE_START,          // [전투 시작 시]
    ON_WIN_CLASH,             // [합 승리시], [합 승리 시]
    ON_WIN_CLASH_HIT,         // [합 승리 적중시], [합 승리 적중 시]
    ON_LOSE_CLASH,            // [합 패배시], [합 패배 시]
    ON_DROP,                  // [이 스킬이 버려지면]
    ON_ATTACK_END,            // [공격 종료 시]
    ON_KILL,                  // [적 처치 시]
    ON_CONDITION_MET          // 예외 조건: 명시된 조건 충족 시
}
