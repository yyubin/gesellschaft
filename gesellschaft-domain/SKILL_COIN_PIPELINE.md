# 스킬-코인 파이프라인 POJO 구현

## 개요

림버스 컴퍼니의 **스킬-동기화-코인-효과** 구조를 헥사고날 아키텍처의 순수 POJO 도메인 객체로 구현한 설계입니다.

---

## 전체 구조

```
Skill (Aggregate Root)
 ├─ id: Long (nullable)
 ├─ name: String
 ├─ skillNumber: int (1, 2, 3)
 ├─ skillCategory: ATTACK / DEFENSE
 ├─ sinAffinity: 죄악 속성
 ├─ keywordType: 출혈, 화상 등
 ├─ attackType / defenseType
 └─ statsBySync: List<SkillStatsBySync>
      │
      ├─ SkillStatsBySync (Entity)
      │   ├─ syncLevel: SYNC_1 ~ SYNC_4
      │   ├─ basePower, coinPower, coinCount, weight, level
      │   ├─ skillEffects: List<SkillEffect>       (스킬 전체 효과)
      │   └─ skillCoins: List<SkillCoin>           (코인별 효과)
      │        │
      │        ├─ SkillCoin (Entity)
      │        │   ├─ orderIndex: int (0부터)
      │        │   ├─ coinType: NORMAL / UNBREAKABLE
      │        │   └─ coinEffects: List<CoinEffect>
      │        │        │
      │        │        └─ CoinEffect (Aggregate Root)
      │        │            ├─ trigger: ON_HIT, ON_HEAD_HIT 등
      │        │            ├─ rootCondition: 조건
      │        │            └─ branches: List<EffectBranch>
      │        │                 └─ actions: List<EffectAction>
      │        │
      │        └─ SkillEffect (Aggregate Root)
      │            ├─ trigger: ON_USE, ON_WIN_CLASH 등
      │            ├─ rootCondition: 조건
      │            └─ branches: List<EffectBranch>
      │                 └─ actions: List<EffectAction>
```

---

## 주요 클래스

### 1. **Skill** (Aggregate Root)

```java
public final class Skill {
    private final Long id;
    private final String name;
    private final int skillNumber;              // 1, 2, 3
    private final SkillCategoryType skillCategory;
    private final SinAffinity sinAffinity;
    private final KeywordType keywordType;
    private final Integer skillQuantity;        // 코인 수 (nullable)
    private final AttackType attackType;        // nullable
    private final DefenseType defenseType;      // nullable
    private final String skillImage;
    private final List<SkillStatsBySync> statsBySync;  // 불변
}
```

**특징:**
- 완전 불변 객체
- 변경 시 새 객체 반환 (`changeImage()`, `addStats()`)
- `getStatsBySyncLevel(SyncLevel)` → `Optional<SkillStatsBySync>`

---

### 2. **SkillStatsBySync** (Entity)

동기화 레벨(1~4)별로 다른 스탯과 효과를 보유

```java
public final class SkillStatsBySync {
    private final Long id;
    private final SyncLevel syncLevel;          // SYNC_1 ~ SYNC_4
    private final int basePower;                // 기본 위력
    private final int coinPower;                // 코인 위력
    private final int coinCount;                // 코인 개수
    private final int weight;                   // 가중치
    private final int level;                    // 공격/방어 레벨
    private final List<SkillEffect> skillEffects;    // 스킬 전체 효과
    private final List<SkillCoin> skillCoins;        // 코인별 효과
}
```

**역할:**
- **스킬 효과**: `[사용시]`, `[합 승리시]` 등 스킬 전체에 적용
- **코인 효과**: 각 코인별 `[적중시]`, `[앞면 적중시]` 효과

---

### 3. **SkillCoin** (Entity)

각 코인의 타입과 효과

```java
public final class SkillCoin {
    private final Long id;
    private final int orderIndex;               // 코인 순서 (0부터)
    private final CoinType coinType;            // NORMAL, UNBREAKABLE
    private final List<CoinEffect> coinEffects; // 코인 효과 목록
}
```

**CoinType:**
- `NORMAL`: 일반 코인
- `UNBREAKABLE`: 불파괴 코인
- `REUSE`: 재사용 코인 (특수)

---

### 4. **SkillEffect** vs **CoinEffect**

| 구분 | SkillEffect | CoinEffect |
|------|-------------|------------|
| **소속** | SkillStatsBySync | SkillCoin |
| **트리거** | ON_USE, ON_WIN_CLASH 등 | ON_HIT, ON_HEAD_HIT, ON_TAIL_HIT 등 |
| **범위** | 스킬 전체 | 특정 코인 |
| **구조** | Trigger → Condition → Branches → Actions | 동일 |

**예시:**
```java
// SkillEffect: 스킬 전체
var skillEffect = SkillEffect.builder(
        new SkillTrigger(TriggerCode.ON_USE, null)
    )
    .branches(...)
    .build();

// CoinEffect: 특정 코인
var coinEffect = CoinEffect.builder(
        new SkillTrigger(TriggerCode.ON_HEAD_HIT, null)
    )
    .branches(...)
    .build();
```

---

## 실제 사용 예시

### 예시 1: 기본 공격 스킬 (3코인)

**스킬**: "잔혹한 참격"
- **[사용시]** 대상의 출혈 3 당 코인 위력 +1 (최대 3)
- **코인 1**: [적중시] 출혈 2 부여
- **코인 2**: [적중시] 출혈 3 부여
- **코인 3**: [앞면 적중시] 출혈 5 부여

```java
// 스킬 효과 (전체)
var skillEffect = SkillEffect.builder(
        new SkillTrigger(TriggerCode.ON_USE, null)
    )
    .originalText("[사용시] 대상의 출혈 3 당 코인 위력 +1 (최대 3)")
    .branches(EffectBranch.builder()
        .actions(EffectAction.builder(ActionType.COIN_POWER_UP, "COIN_POWER")
            .formulaAmount("TARGET.BLEED / 3", ActionUnit.FLAT)
            .cap(3)
            .scope(ActionScope.COINS_SKILL)
            .build())
        .build())
    .build();

// 코인 1 효과
var coin1Effect = CoinEffect.builder(
        new SkillTrigger(TriggerCode.ON_HIT, null)
    )
    .branches(EffectBranch.builder()
        .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
            .flatAmount(2, ActionUnit.STACK)
            .targetSelector(TargetSelector.allEnemies())
            .build())
        .build())
    .build();

var coin1 = SkillCoin.builder(0)
    .coinType(CoinType.NORMAL)
    .coinEffects(coin1Effect)
    .build();

// 동기화 레벨 스탯
var statsBySync3 = SkillStatsBySync.builder(SyncLevel.SYNC_3)
    .basePower(4)
    .coinPower(6)
    .coinCount(3)
    .skillEffects(List.of(skillEffect))
    .skillCoins(List.of(coin1, coin2, coin3))
    .build();

// 최종 스킬
var skill = Skill.builder()
    .name("잔혹한 참격")
    .skillNumber(1)
    .skillCategory(SkillCategoryType.ATTACK)
    .sinAffinity(SinAffinity.WRATH)
    .keywordType(KeywordType.BLEED)
    .skillQuantity(3)
    .attackType(AttackType.SLASH)
    .statsBySync(List.of(statsBySync3))
    .build();
```

---

### 예시 2: 조건부 코인 효과

**코인 1**: [적중시] 충전 5 이상이면 위력 +3, **대신** 충전 3 부여

```java
var condition = ConditionGroup.and(
    StatCondition.of(ConditionTarget.SELF, "CHARGE",
        ConditionOperator.GREATER_THAN_OR_EQUAL, 5)
);

// Branch 1: 충전 5+ → 위력 +3
var branch1 = EffectBranch.builder()
    .condition(condition)
    .actions(EffectAction.builder(ActionType.POWER_MODIFY, "POWER")
        .flatAmount(3, ActionUnit.FLAT)
        .scope(ActionScope.THIS_COIN)
        .build())
    .stopOnMatch()  // "대신" 키워드 → 다음 브랜치 스킵
    .build();

// Branch 2: 기본 → 충전 3 부여
var branch2 = EffectBranch.defaultBranch(
    List.of(EffectAction.builder(ActionType.STATUS_INFLICT, "CHARGE")
        .flatAmount(3, ActionUnit.STACK)
        .targetSelector(TargetSelector.self())
        .build()),
    1
);

var coinEffect = CoinEffect.builder(
        new SkillTrigger(TriggerCode.ON_HIT, null)
    )
    .branches(branch1, branch2)
    .build();

var coin = SkillCoin.builder(0)
    .coinEffects(coinEffect)
    .build();
```

---

### 예시 3: 동적 계산식

**코인 2**: [앞면 적중시] 대상에게 진동 (자신의 충전/2) 부여

```java
var coinEffect = CoinEffect.builder(
        new SkillTrigger(TriggerCode.ON_HEAD_HIT, null)
    )
    .branches(EffectBranch.builder()
        .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "TREMOR")
            .formulaAmount("SELF.CHARGE / 2", ActionUnit.STACK)
            .targetSelector(TargetSelector.allEnemies())
            .build())
        .build())
    .build();
```

---

## 핵심 설계 패턴

### 1. **SkillEffect vs CoinEffect 분리**

**이유:**
- 스킬 효과: 스킬 사용 시점/합 승리 시점 등
- 코인 효과: 각 코인별 적중 시점

**장점:**
- 명확한 책임 분리
- 코인별 독립적인 효과 정의 가능
- 불파괴 코인, 특수 코인 타입 지원

---

### 2. **불변성 보장**

```java
// ❌ 가변 (위험)
skill.getStatsBySync().add(newStats);  // 직접 수정

// ✅ 불변 (안전)
var updated = skill.addStats(newStats);  // 새 객체 반환
```

---

### 3. **동기화 레벨별 차등 스탯**

```java
var sync3 = SkillStatsBySync.builder(SyncLevel.SYNC_3)
    .basePower(3)
    .coinPower(5)
    .build();

var sync4 = SkillStatsBySync.builder(SyncLevel.SYNC_4)
    .basePower(4)
    .coinPower(7)
    .build();

var skill = Skill.builder()
    .statsBySync(List.of(sync3, sync4))
    .build();

// 조회
skill.getStatsBySyncLevel(SyncLevel.SYNC_3)
    .ifPresent(stats -> {
        System.out.println("Base Power: " + stats.getBasePower());
    });
```

---

### 4. **트리거별 효과 관리**

```java
// 스킬 전체: [사용시], [합 승리시] 등
skillStats.getSkillEffects()
    .stream()
    .filter(e -> e.getTrigger().getCode() == TriggerCode.ON_USE)
    .forEach(...);

// 특정 코인: [적중시], [앞면 적중시] 등
coin.getEffectsByTrigger("ON_HEAD_HIT")
    .forEach(...);
```

---

## 파이프라인 실행 흐름

```
1. 스킬 사용
   ↓
2. SkillEffect (ON_USE) 실행
   - 조건 검사 (rootCondition)
   - 브랜치 순회 (order 순)
   - 액션 실행
   ↓
3. 코인 1 실행
   ↓
4. CoinEffect (ON_HIT / ON_HEAD_HIT) 실행
   - 조건 검사
   - 브랜치 실행
   ↓
5. 코인 2 실행
   ...
   ↓
6. 공격 종료
   ↓
7. SkillEffect (ON_ATTACK_END) 실행
```

---

## 장점

| 항목 | 설명 |
|------|------|
| **명확한 구조** | Skill → Stats → Coin → Effect |
| **불변성** | 모든 객체 불변, 버그 방지 |
| **동기화 레벨 지원** | 각 레벨별 다른 수치 |
| **코인별 효과** | 각 코인 독립적 효과 |
| **동적 계산식** | formula 필드로 복잡한 수식 지원 |
| **조건부 분기** | stopOnMatch로 "대신" 키워드 표현 |
| **확장성** | 새 트리거/액션 추가 용이 |

---

## 📂 파일 구조

```
model/skill/
├── Skill.java                      (Aggregate Root)
├── SkillStatsBySync.java           (Entity)
├── SkillCoin.java                  (Entity)
├── SyncLevel.java                  (Enum)
├── CoinType.java                   (Enum)
├── SkillCategoryType.java          (Enum)
├── DefenseType.java                (Enum)
├── TriggerCode.java                (Enum)
├── SkillTrigger.java               (Value Object)
└── SkillCoinPipelineExample.java   (예시 코드)

model/skill/effect/
├── SkillEffect.java                (Aggregate Root)
├── CoinEffect.java                 (Aggregate Root)
├── EffectBranch.java               (Value Object)
├── EffectAction.java               (Value Object)
├── TargetSelector.java             (Value Object)
├── CoinSelector.java               (Value Object)
└── ... (Enums)
```

---

## 다음 단계

1. **JPA 매퍼 작성** (`gesellschaft-infrastructure`)
   - Skill → SkillJpa 변환
   - JSON 직렬화 (PostgreSQL JSONB 고려)

2. **Repository 구현**
   - `SkillRepository.findByPersonaIdAndSyncLevel()`
   - 효과 트리 쿼리 최적화

3. **계산식 파서**
   - `"SELF.CHARGE / 2"` → 실제 값 계산
   - SpEL 또는 커스텀 파서

4. **DSL 설계**
   - 텍스트 → 도메인 객체
   - 관리자 UI에서 GUI 빌더

---

## 핵심 요약

1. ✅ **Skill은 여러 SkillStatsBySync 보유** (동기화 레벨별)
2. ✅ **SkillStatsBySync는 SkillEffect + SkillCoin 보유**
3. ✅ **SkillCoin은 여러 CoinEffect 보유** (코인별 효과)
4. ✅ **SkillEffect와 CoinEffect는 동일 구조** (Trigger → Condition → Branch → Action)
5. ✅ **완전 불변 객체** (변경 시 새 객체 반환)
6. ✅ **Optional은 getter에서만 사용** (필드는 nullable)

---

## 참고

- **예시 코드**: `SkillCoinPipelineExample.java`
- **효과 시스템**: `SkillEffectExamples.java`
- **리팩토링 가이드**: `POJO_REFACTORING_GUIDE.md`
