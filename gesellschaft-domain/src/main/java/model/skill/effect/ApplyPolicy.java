package model.skill.effect;

/**
 * 효과 적용 정책
 */
public enum ApplyPolicy {
    ADD,                // 덧셈
    MULTIPLY,           // 곱셈
    OVERRIDE,           // 덮어쓰기
    SUPPRESS,           // 기존 효과 억제
    REPLACE_OUTCOME,    // 결과 대체
    SET                 // 고정값 설정
}
