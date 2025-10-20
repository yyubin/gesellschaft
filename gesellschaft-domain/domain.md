# 도메인 스펙 문서 (domain.md)

> 게젤샤프트(Gesellschaft) 프로젝트의 도메인 모델 규격 및 개념 정의를 정리한 문서입니다.
>
> 이 문서는 JPA나 스프링 같은 기술 세부사항과는 분리된 **순수 도메인 계층 기준의 개념 명세서**입니다.

---

## 1. Sinner (시너)

시너(Sinner)는 게임 내 고정된 12명의 캐릭터를 의미합니다. 각 시너는 여러 개의 인격(Persona)을 가질 수 있으며, 인격을 통해 다양한 전투 스타일이나 능력치를 갖습니다.

| 필드명        | 타입              | 설명                              |
| ---------- | --------------- | ------------------------------- |
| `id`       | `Long`          | 시너 고유 식별자                       |
| `name`     | `String`        | 시너의 한국어 이름                      |
| `nameEn`   | `String`        | 시너의 영문 이름 (내부 코드명, 다국어 키 역할 가능) |
| `personas` | `List<Persona>` | 해당 시너가 보유한 인격 목록                |

---

## 2. Persona (인격)

인격(Persona)은 시너의 변형된 버전, 혹은 전투 스타일에 해당합니다. 인격은 고유의 등급(Grade), 능력치, 출시 시즌, 그리고 보유 스킬 세트를 가집니다.

| 필드명              | 타입               | 설명                                    |
| ---------------- | ---------------- | ------------------------------------- |
| `id`             | `Long`           | 인격 고유 식별자                             |
| `name`           | `String`         | 인격의 한국어 이름                            |
| `nameEn`         | `String`         | 인격의 영문 이름                             |
| `grade`          | `GradeType`      | 인격 등급 (1성~3성)                         |
| `releaseDate`    | `LocalDate`      | 출시일                                   |
| `skills`         | `List<Skill>`    | 인격이 보유한 스킬 목록 (동기화 레벨 1~4별로 달라질 수 있음) |
| `resistanceInfo` | `ResistanceInfo` | 속성별 공격 내성 정보                          |
| `speedInfo`      | `SpeedInfo`      | 속도 정보 (최소/최대)                         |
| `healthInfo`     | `HealthInfo`     | 체력 및 성장 정보                            |
| `seasonInfo`     | `SeasonInfo`     | 시즌/이벤트 정보                             |
| `maxLevel`       | `int`            | 인격의 최대 성장 레벨 (기본값 55)                 |

---

## 3. GradeType (등급)

```java
public enum GradeType {
    ONE, TWO, THREE;
}
```

| Enum 값  | 별 표시 | 의미                 |
| ------- | ---- | ------------------ |
| `ONE`   | ★1   | 1성 인격 (기본, 낮은 희귀도) |
| `TWO`   | ★2   | 2성 인격 (중간 희귀도)     |
| `THREE` | ★3   | 3성 인격 (고희귀, 대표 인격) |

---

## 4. ResistanceInfo (내성 정보)

각 인격이 특정 공격 타입(참격, 관통, 타격)에 대해 가지는 내성 수치를 표현합니다.

| 필드명                     | 타입               | 설명           |
| ----------------------- | ---------------- | ------------ |
| `slashResistance`       | `ResistanceType` | 참격 공격에 대한 내성 |
| `penetrationResistance` | `ResistanceType` | 관통 공격에 대한 내성 |
| `bluntResistance`       | `ResistanceType` | 타격 공격에 대한 내성 |

### 관련 Enum: ResistanceType

```java
public enum ResistanceType {
    NORMAL, // 보통
    WEAK,   // 취약
    RESIST  // 내성
}
```

| Enum 값   | 의미            | 피해 배율 |
| -------- | ------------- | ----- |
| `NORMAL` | 보통 내성         | ×1.0  |
| `WEAK`   | 취약, 피해량 증가    | ×1.5  |
| `RESIST` | 강한 내성, 피해량 감소 | ×0.5  |

> ⚙️ `ResistanceInfo.getDamageMultiplier(AttackType)` 메서드를 통해 자동 계산 가능.

---

## 5. AttackType (공격 타입)

공격의 형태(물리 속성)를 구분하는 Enum입니다. 이는 스킬의 타입과도 1:1 대응됩니다.

```java
public enum AttackType {
    SLASH,  // 참격
    PIERCE, // 관통
    BLUNT   // 타격
}
```

| Enum 값   | 한글명 | 설명           |
| -------- | --- | ------------ |
| `SLASH`  | 참격  | 날붙이, 베기류 공격  |
| `PIERCE` | 관통  | 창, 총기류 등의 공격 |
| `BLUNT`  | 타격  | 둔기, 폭발류 공격   |

---

## 6. SpeedInfo (속도 정보)

| 필드명        | 타입    | 설명    |
| ---------- | ----- | ----- |
| `minSpeed` | `int` | 최소 속도 |
| `maxSpeed` | `int` | 최대 속도 |

---

## 7. HealthInfo (체력 정보)

| 필드명          | 타입       | 설명          |
| ------------ | -------- | ----------- |
| `baseHealth` | `int`    | 기본 체력       |
| `growthRate` | `double` | 레벨당 성장 계수   |
| `disturbed1` | `int`    | 흐트러짐 1단계 체력 |
| `disturbed2` | `int`    | 흐트러짐 2단계 체력 |
| `disturbed3` | `int`    | 흐트러짐 3단계 체력 |

> 실제 체력 = `baseHealth + growthRate * (level - 1)` 형태로 계산됩니다.

---

## 8. SeasonInfo (시즌 정보)

인격의 출시 시즌 또는 이벤트 시즌 정보를 나타냅니다.

| 필드명          | 타입           | 설명                           |
| ------------ | ------------ | ---------------------------- |
| `seasonType` | `SeasonType` | 시즌 구분 (통상, 시즌, 이벤트, 발푸르기스 등) |
| `number`     | `Integer`    | 시즌 넘버링 (예: 1시즌, 2시즌)         |

### 관련 Enum: SeasonType

```java
public enum SeasonType {
    NORMAL("통상"),
    SEASON_NORMAL("시즌"),
    SEASON_EVENT("시즌 이벤트"),
    WALPURGISNACHT("발푸르기스");

    private final String name;

    SeasonType(String name) {
        this.name = name;
    }
}
```

| Enum 값           | 한글명    | 의미                     |
| ---------------- | ------ | ---------------------- |
| `NORMAL`         | 통상     | 기본 시즌 (기본 라인업)         |
| `SEASON_NORMAL`  | 시즌     | 일반 시즌 배포 콘텐츠           |
| `SEASON_EVENT`   | 시즌 이벤트 | 특정 이벤트 한정 인격           |
| `WALPURGISNACHT` | 발푸르기스  | 특수 테마 시즌 (보스/콜라보성 콘텐츠) |

---

## 9. 관계 요약 (간략 UML)

```
Sinner
 └── Persona (1:N)
      ├── GradeType (enum)
      ├── ResistanceInfo
      │     └── ResistanceType (enum)
      ├── AttackType (enum)
      ├── SpeedInfo
      ├── HealthInfo
      └── SeasonInfo
            └── SeasonType (enum)
```

---

## 10. 용어 일관성 검토

| 개념    | 코드 명칭                               | 문서 표기   | 일관성 |
| ----- | ----------------------------------- | ------- | --- |
| 시너    | `Sinner`                            | Sinner  | ✅   |
| 인격    | `Persona`                           | Persona | ✅   |
| 등급    | `GradeType`                         | 등급      | ✅   |
| 내성    | `ResistanceInfo` / `ResistanceType` | 내성 정보   | ✅   |
| 공격 타입 | `AttackType`                        | 공격 타입   | ✅   |
| 속도    | `SpeedInfo`                         | 속도 정보   | ✅   |
| 체력    | `HealthInfo`                        | 체력 정보   | ✅   |
| 시즌    | `SeasonInfo` / `SeasonType`         | 시즌 정보   | ✅   |

> 전체적으로 네이밍과 용어가 일관적이며, 코드-도메인 간 의미 불일치 없음.

---

## 11. 향후 확장 항목

* [ ] `Skill` 도메인 추가 (SinAffinity, CoinSpec 등 포함)
* [ ] `PersonaSync` 구조 확정 (동기화 레벨별 스킬 차등)
* [ ] `ResistanceType` 배율 설정을 BalanceConfig 등 외부 설정화 고려
* [ ] 전투 계산 도메인에 `AttackType` 기반 상성 시스템 도입 검토
