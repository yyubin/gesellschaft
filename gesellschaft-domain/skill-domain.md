# Skill Domain Spec (skill-domain.md)

> 게젤샤프트(Gesellschaft) 프로젝트의 스킬 및 동기화 도메인 설계 문서입니다.
> Persona가 보유하는 전투 스킬 구조를 정의하며, 각 스킬의 동기화 레벨별 능력치를 별도 엔티티로 관리합니다.

---

## 1. Skill (스킬)

`Skill`은 인격(Persona)의 개별 전투 기술을 표현합니다.
스킬은 공격/방어/보조 등 다양한 분류를 가질 수 있으며, 각 스킬은 여러 개의 동기화 레벨(`SyncLevel`)에 따른 세부 능력치를 보유합니다.

| 필드명                | 타입                       | 설명                        |
| ------------------ | ------------------------ | ------------------------- |
| `id`               | `Long`                   | 스킬 고유 식별자                 |
| `skillNumber`      | `int`                    | 인격 내 스킬 번호 (1~3번 스킬)      |
| `name`             | `String`                 | 스킬 이름                     |
| `skillCategory`    | `SkillCategoryType`      | 스킬 분류 (공격/방어 등)           |
| `skillAttribute`   | `SkillAttributeType`     | 스킬 속성 (분노, 탐식, 우울 등)      |
| `skillKeywordType` | `SkillKeywordType`       | 부가 효과 키워드 (출혈, 화상 등)      |
| `skillQuantity`    | `Integer`                | 공격 스킬 수량 (예: 2코인 스킬)      |
| `attackType`       | `AttackType`             | 공격 스킬의 물리 속성 (참격, 관통, 타격) |
| `defenseType`      | `DefenseType`            | 방어 스킬의 세부 타입 (보호, 반격 등)   |
| `persona`          | `Persona`                | 스킬을 보유한 인격                |
| `skillImage`       | `String`                 | 스킬 이미지 URL (관리용)          |
| `statsBySync`      | `List<SkillStatsBySync>` | 동기화 레벨별 스탯 정보 목록          |

---

### 📘 Enum: SkillCategoryType

| Enum 값    | 의미             |
| --------- | -------------- |
| `ATTACK`  | 공격 스킬          |
| `DEFENSE` | 방어 스킬          |

### 📘 Enum: SinAffinity

| Enum 값     | 의미   |
| ---------- |------|
| `WRATH`    | 분노   |
| `LUST`     | 색욕   |
| `SLOTH`    | 나태   |
| `GREED` | 탐식   |
| `GLOOM`    | 우울   |
| `PRIDE`    | 오만   |
| `ENVY`     | 질투   |
| `NONE`     | 없음   |

### 📘 Enum: KeywordType

| Enum 값    | 의미 |
| --------- |----|
| `BURN`    | 화상 |
| `BLEED`   | 출혈 |
| `TREMOR`  | 진동 |
| `RUPTURE`  | 파열 |
| `SINKING`    | 침잠 |
| `BREATH`   | 호흡 |
| `CHARGE`   | 충전 |
| `NONE`    | 없음 |

### 📘 Enum: DefenseType (방어 타입)
| Enum 값    | 설명 |
| --------- | -- |
| `COUNTER` | 반격 |
| `EVADE`   | 회피 |
| `GUARD`   | 방어 |


---

## 2. SkillStatsBySync (동기화별 스킬 수치)

하나의 스킬이 각 동기화 레벨(`SyncLevel`)에서 가지는 능력치를 정의합니다.
스킬의 위력, 코인 개수, 공격 레벨, 효과 목록 등은 이 엔티티에 저장됩니다.

| 필드명            | 타입                  | 설명              |
| -------------- | ------------------- | --------------- |
| `id`           | `Long`              | 식별자             |
| `skill`        | `Skill`             | 소속된 스킬          |
| `syncLevel`    | `SyncLevel`         | 동기화 레벨 (1~4)    |
| `basePower`    | `int`               | 기본 위력           |
| `coinPower`    | `int`               | 코인 위력           |
| `coinCount`    | `int`               | 코인 개수           |
| `weight`       | `int`               | 행동 가중치          |
| `level`        | `int`               | 공격/방어 레벨        |
| `skillEffects` | `List<SkillEffect>` | 부가 효과 목록        |
| `skillCoins`   | `List<SkillCoin>`   | 코인별 상세 수치 (선택적) |

---

## 3. SyncLevel (동기화 레벨)

```java
public enum SyncLevel {
    ONE, TWO, THREE, FOUR;
}
```

| Enum 값  | 표시    | 설명             |
| ------- | ----- | -------------- |
| `ONE`   | 동기화 1 | 기본 상태          |
| `TWO`   | 동기화 2 | 강화된 상태         |
| `THREE` | 동기화 3 | 고레벨 인격 기준      |
| `FOUR`  | 동기화 4 | 완전 동기화 (최대 스탯) |

---

## 4. SkillEffect (스킬 효과)

> 🔸 상세 설계는 후속 확장으로 분리 예정.

| 필드명          | 타입           | 설명               |
| ------------ | ------------ | ---------------- |
| `effectType` | `EffectType` | 효과 유형 (출혈, 화상 등) |
| `potency`    | `int`        | 강도               |
| `duration`   | `int`        | 지속 턴             |

---

## 5. SkillCoin (코인 수치)

> 🔸 실제 전투 연산을 위해 각 코인의 위력 및 확률을 저장하는 하위 엔티티.

| 필드명         | 타입       | 설명                 |
| ----------- | -------- | ------------------ |
| `index`     | `int`    | 코인 순서 (1, 2, 3...) |
| `power`     | `int`    | 코인 위력              |
| `condition` | `String` | 조건부 발동 (선택적)       |

---

## 6. 도메인 관계 구조

```
Persona
 └── Skill (1:N)
       ├── SkillStatsBySync (1:N)
       │     ├── SkillEffect (1:N)
       │     └── SkillCoin (1:N)
       └── SyncLevel (enum)
```

---

## 7. 설계 의도 요약

| 항목             | 설명                                                   |
| -------------- | ---------------------------------------------------- |
| **도메인 정체성 분리** | 스킬 자체(`Skill`)와 상태(`SkillStatsBySync`)를 분리하여 유지보수 용이 |
| **밸런스 패치 대응성** | 스탯/코인/효과 변경 시 상위 구조 영향 최소화                           |
| **데이터 정규화**    | 동일 스킬의 중복 정의를 방지                                     |
| **도메인 응집도**    | Skill 단위로 Aggregate를 형성하여 일관된 관리 가능                  |
| **확장성 확보**     | 코인 효과, 추가 Sync 레벨, 버프/디버프 등 확장 용이                    |

---

## 8. 향후 확장 계획

* [ ] SkillEffect 상세 정의 (EffectType, TriggerCondition 등)
* [ ] SkillCoin 전투 계산 로직 반영
* [ ] DTO 분리 및 조회 전용 Projection 설계
