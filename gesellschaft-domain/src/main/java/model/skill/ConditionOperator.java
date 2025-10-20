package model.skill;

/**
 * 조건 비교 연산자
 */
public enum ConditionOperator {
    EQUAL,                      // ==
    NOT_EQUAL,                  // !=
    GREATER_THAN,               // >
    GREATER_THAN_OR_EQUAL,      // >=
    LESS_THAN,                  // <
    LESS_THAN_OR_EQUAL,         // <=
    IN_RANGE,                   // BETWEEN
    DIVISIBLE_BY,               // % N == 0 (N당)
    HAS_TAG,                    // 태그 보유 여부
    HAS_STATUS                  // 상태 이상 보유 여부
}
