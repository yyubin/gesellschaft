# Skill-Coin Pipeline Overview

## 1. 전체 구조 요약

```
Skill
 ├─ SkillStatsBySync (SYNC_3, SYNC_4 …)
 │   ├─ SkillCoin [1..n]
 │   │   └─ SkillCoinEffect (AbstractEffect 상속)
 │   └─ SkillEffect (스킬 전체 효과)
 │
 └─ Meta (Effect / Trigger / Condition / Action / Selector)
```

스킬은 ‘싱크 레벨(SyncLevel)’별로 스펙이 분기되며,
각 레벨에는 **코인(coin)**이 여러 개 있고,
각 코인에는 **효과(effect)**가 매핑되어 있다.

---

## 2. 실행 파이프라인 (Trigger → Condition → Branch → Action)

```
[1] Trigger 발생
      ↓
[2] Root Condition 검사
      ↓ (true)
[3] Branch 순회 (order 순)
      ↓
[4] ConditionGroup.evaluate(ctx)
      ↓ (true)
[5] EffectAction.execute(ctx)
      ↓
[6] Selector로 타겟 선택 후 적용
```

| 단계 | 컴포넌트                            | 설명                                                                |
| -- | ------------------------------- | ----------------------------------------------------------------- |
| 1  | **SkillTrigger**                | 트리거 조건을 표현. 예: `ON_HIT`, `ON_USE`, `ON_WIN_CLASH`                 |
| 2  | **ConditionGroup**              | 트리거가 발생한 시점의 전투 상태를 검사. `AND` / `OR` 트리 형태                        |
| 3  | **EffectBranch**                | 각 조건에 따라 다른 행동 세트를 실행. `order` / `stopOnMatch` 로 제어               |
| 4  | **EffectAction**                | 구체적인 동작: 스탯 변화, 상태 이상, 버프/디버프 등                                   |
| 5  | **TargetSelector**              | 어떤 유닛에게 적용할지를 결정 (`SELF`, `ENEMY`, `ALLY`, …)                     |
| 6  | **ConditionStat / ApplyPolicy** | 적용 방식(`ADD`, `MULTIPLY`, `OVERRIDE`, …)과 단위(`FLAT`, `PERCENT`) 정의 |

---

## 3. SkillCoin 파이프라인

```
SkillStatsBySync
 ├─ SkillCoin[1]  → CoinType.NORMAL
 │    └─ SkillCoinEffect(trigger=ON_HIT, ...)
 ├─ SkillCoin[2]  → CoinType.UNBREAKABLE
 │    └─ SkillCoinEffect(trigger=ON_HEAD_HIT, ...)
 └─ SkillCoin[3]  → ...
```

* 코인은 순서대로 평가됨 (`orderIndex` 추가 예정)
* 각 코인에는 고유한 **Trigger + Condition + Action** 트리가 붙을 수 있음
* 예: “첫 번째 코인은 적중 시 출혈 2부여”, “두 번째 코인은 앞면 시 코인 위력 +2”

---

## 4. 예시 시퀀스

> “이 스킬을 사용했을 때” (ON_USE → ON_HIT → ON_CRITICAL_HIT)

1. **Trigger**: `ON_USE` → 스킬 사용 시작 시 발동
2. **RootCondition**: “자신의 체력이 50% 이하일 때”
3. **Branch1**: 조건 참 → `공명수 3이상인 적에게 출혈 2부여`
4. **Selector**: `ENEMY`, `filterStat=RESONANCE_COUNT ≥ 3`
5. **Action**: `statAffected=BLEED`, `amount=2`, `unit=FLAT`, `timing=IMMEDIATE`

---

## 5. 설계 철학

| 원칙          | 설명                                               |
| ----------- | ------------------------------------------------ |
| **확장성**     | 새로운 Trigger/Action/ConditionType을 추가해도 구조 변경 최소화 |
| **가시성**     | ConditionGroup 트리를 직렬화하면 효과 조건을 직관적으로 확인 가능      |
| **데이터 주도성** | Effect 구조를 JSON 또는 DSL 형태로 선언하면 동적 로딩 가능         |
| **POJO 우선** | 영속성보다 도메인 로직을 중심으로 설계 → 시뮬레이터/에디터 통합 용이          |

