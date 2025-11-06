# JPA 엔티티 문서

## 목차
1. [개요](#개요)
2. [엔티티 계층 구조](#엔티티-계층-구조)
3. [전체 엔티티 관계도](#전체-엔티티-관계도)
4. [엔티티 상세 정의](#엔티티-상세-정의)
   - [코어 엔티티](#1-코어-엔티티)
   - [스킬 관련 엔티티](#2-스킬-관련-엔티티)
   - [패시브 관련 엔티티](#3-패시브-관련-엔티티)
   - [효과 시스템 엔티티](#4-효과-시스템-엔티티)
   - [조건 시스템 엔티티](#5-조건-시스템-엔티티)
5. [주요 설계 패턴](#주요-설계-패턴)
6. [데이터베이스 매핑 전략](#데이터베이스-매핑-전략)

---

## 개요

이 문서는 Gesellschaft 프로젝트의 `gesellschaft-infrastructure` 모듈에 정의된 JPA 엔티티들을 상세히 설명합니다.

**프로젝트 특성:**
- 림버스 컴퍼니 게임 데이터 아카이브 및 서비스
- 헥사고날 아키텍처 기반
- POJO 도메인 모델 ↔ JPA 엔티티 매핑 전략
- Jinx 사용한 DDL 생성 및 마이그레이션

**엔티티 개수:** 총 16개

---

## 엔티티 계층 구조

```
SinnerJpa (시너)
└── PersonaJpa (인격) ★ Aggregate Root
    ├── PersonaImageJpa (이미지)
    ├── PersonaPassiveJpa (패시브)
    │   └── PassiveEffectJpa (패시브 효과)
    │       ├── ConditionGroupJpa (조건)
    │       └── EffectBranchJpa (효과 분기)
    │           └── EffectActionJpa (액션)
    └── SkillJpa (스킬)
        └── SkillStatsBySyncJpa (동기화 레벨별 스탯)
            ├── SkillEffectJpa (스킬 효과)
            │   ├── ConditionGroupJpa (조건)
            │   └── EffectBranchJpa (효과 분기)
            │       └── EffectActionJpa (액션)
            └── SkillCoinJpa (스킬 코인)
                └── CoinEffectJpa (코인 효과)
                    ├── ConditionGroupJpa (조건)
                    └── EffectBranchJpa (효과 분기)
                        └── EffectActionJpa (액션)

조건 계층 (JOINED 상속)
AbstractConditionJpa (추상)
├── ConditionGroupJpa (복합 조건 - AND/OR)
│   └── children: AbstractConditionJpa[] (재귀)
├── StatConditionJpa (스탯 조건 - 리프)
└── RangeConditionJpa (범위 조건 - 리프)
```

---

## 전체 엔티티 관계도

```mermaid
erDiagram
    SINNER ||--o{ PERSONA : "has"
    PERSONA ||--o{ PERSONA_IMAGE : "has"
    PERSONA ||--o{ PERSONA_PASSIVE : "has"
    PERSONA ||--o{ SKILL : "has"

    PERSONA_PASSIVE ||--|| PASSIVE_EFFECT : "has"
    PASSIVE_EFFECT }o--|| CONDITION_GROUP : "root condition"
    PASSIVE_EFFECT ||--o{ EFFECT_BRANCH : "branches"

    SKILL ||--o{ SKILL_STATS_BY_SYNC : "sync levels"
    SKILL_STATS_BY_SYNC ||--o{ SKILL_EFFECT : "effects"
    SKILL_STATS_BY_SYNC ||--o{ SKILL_COIN : "coins"

    SKILL_COIN ||--o{ COIN_EFFECT : "effects"

    SKILL_EFFECT }o--|| CONDITION_GROUP : "root condition"
    SKILL_EFFECT ||--o{ EFFECT_BRANCH : "branches"

    COIN_EFFECT }o--|| CONDITION_GROUP : "root condition"
    COIN_EFFECT ||--o{ EFFECT_BRANCH : "branches"

    EFFECT_BRANCH }o--|| CONDITION_GROUP : "condition"
    EFFECT_BRANCH ||--o{ EFFECT_ACTION : "actions"

    CONDITION_GROUP ||--o{ ABSTRACT_CONDITION : "children (recursive)"
    ABSTRACT_CONDITION <|-- CONDITION_GROUP : "inherits"
    ABSTRACT_CONDITION <|-- STAT_CONDITION : "inherits"
    ABSTRACT_CONDITION <|-- RANGE_CONDITION : "inherits"
```

---

## 엔티티 상세 정의

### 1. 코어 엔티티

#### 1.1 SinnerJpa

**테이블명:** `sinner`

**설명:** 림버스 컴퍼니의 12명의 고정 시너(Sinner)를 나타냅니다.

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `name` | String(20) | UNIQUE, NOT NULL | 시너 이름 (한글) |
| `nameEn` | String(40) | UNIQUE, NOT NULL | 시너 이름 (영문) |

**관계:**
- `personas`: `List<PersonaJpa>` - **1:N** (mappedBy = "sinner", CASCADE_ALL, orphanRemoval = true)

**양방향 관계 편의 메서드:**
- `addPersona(PersonaJpa persona)`: Persona 추가 및 역방향 관계 설정
- `removePersona(PersonaJpa persona)`: Persona 제거 및 역방향 관계 해제

**도메인 매핑:**
- `ofDomain(model.Sinner)`: 도메인 객체 → JPA 엔티티
- `updateFromDomain(model.Sinner)`: 도메인 객체로 기존 엔티티 업데이트

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/SinnerJpa.java:1`

---

#### 1.2 PersonaJpa

**테이블명:** `persona`

**설명:** 각 시너의 전투 인격(Persona)을 나타냅니다. **Aggregate Root** 역할을 합니다.

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `name` | String(100) | NOT NULL | 인격 이름 (한글) |
| `nameEn` | String(100) | | 인격 이름 (영문) |
| `grade` | GradeType (Enum) | NOT NULL | 등급 (1성, 2성, 3성 등) |
| `releaseDate` | LocalDate | | 출시일 |
| `maxLevel` | int | NOT NULL, DEFAULT 50 | 최대 레벨 |

**내성 정보 (Embedded - ResistanceInfo):**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `slashResistance` | ResistanceType (Enum) | NOT NULL | 참격 내성 |
| `penetrationResistance` | ResistanceType (Enum) | NOT NULL | 관통 내성 |
| `bluntResistance` | ResistanceType (Enum) | NOT NULL | 타격 내성 |

**속도 정보 (Embedded - SpeedInfo):**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `minSpeed` | int | NOT NULL | 최소 속도 |
| `maxSpeed` | int | NOT NULL | 최대 속도 |

**체력 정보 (Embedded - HealthInfo):**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `baseHealth` | int | NOT NULL | 기본 체력 |
| `growthRate` | double | NOT NULL | 성장률 |
| `disturbed1` | int | | 동요 단계 1 |
| `disturbed2` | int | | 동요 단계 2 |
| `disturbed3` | int | | 동요 단계 3 |

**시즌 정보 (Embedded - SeasonInfo):**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `seasonType` | SeasonType (Enum) | | 시즌 타입 |
| `seasonNumber` | Integer | | 시즌 번호 |

**관계:**
- `sinner`: `SinnerJpa` - **N:1** (LAZY, NOT NULL)
- `skills`: `List<SkillJpa>` - **1:N** (mappedBy = "persona", CASCADE_ALL, orphanRemoval = true)
- `passives`: `List<PersonaPassiveJpa>` - **1:N** (mappedBy = "persona", CASCADE_ALL, orphanRemoval = true)
- `images`: `List<PersonaImageJpa>` - **1:N** (mappedBy = "persona", CASCADE_ALL, orphanRemoval = true)

**양방향 관계 편의 메서드:**
- `setSinner(SinnerJpa sinner)`
- `addSkill(SkillJpa skill)` / `removeSkill(SkillJpa skill)`
- `addPassive(PersonaPassiveJpa passive)` / `removePassive(PersonaPassiveJpa passive)`
- `addImage(PersonaImageJpa image)` / `removeImage(PersonaImageJpa image)`

**도메인 매핑:**
- `ofDomain(Persona)`: 도메인 객체 → JPA 엔티티 (내장 VO 포함)
- `updateFromDomain(Persona)`: 도메인 객체로 기존 엔티티 업데이트

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/PersonaJpa.java:1`

---

#### 1.3 PersonaImageJpa

**테이블명:** `persona_image`

**설명:** 인격의 이미지 정보 (아이콘, 전신, 배경 등)

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `type` | ImageType (Enum) | NOT NULL | 이미지 타입 (ICON, FULL, BACKGROUND 등) |
| `url` | String(500) | NOT NULL | 이미지 URL |
| `priority` | int | NOT NULL | 우선순위 (정렬용) |
| `isPrimary` | boolean | NOT NULL | 대표 이미지 여부 |

**관계:**
- `persona`: `PersonaJpa` - **N:1** (LAZY, NOT NULL)

**양방향 관계 편의 메서드:**
- `setPersona(PersonaJpa persona)`

**도메인 매핑:**
- `ofDomain(PersonaImage)`: 도메인 객체 → JPA 엔티티
- `updateFromDomain(PersonaImage)`: 도메인 객체로 기존 엔티티 업데이트

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/PersonaImageJpa.java:1`

---

### 2. 스킬 관련 엔티티

#### 2.1 SkillJpa

**테이블명:** `skill`

**설명:** 인격의 개별 스킬 (스킬 1, 2, 3, 방어 스킬 등)

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `skillNumber` | int | NOT NULL | 스킬 번호 (1, 2, 3, ...) |
| `name` | String(100) | NOT NULL | 스킬 이름 |
| `skillCategory` | SkillCategoryType (Enum) | NOT NULL | 스킬 카테고리 (ATTACK, DEFENSE 등) |
| `sinAffinity` | SinAffinity (Enum) | NOT NULL | 죄악 속성 (WRATH, LUST, ...) |
| `keywordType` | KeywordType (Enum) | NOT NULL | 키워드 타입 |
| `skillQuantity` | Integer | NULLABLE | 스킬 수량 (방어 스킬은 null) |
| `attackType` | AttackType (Enum) | NULLABLE | 공격 타입 (SLASH, PIERCE, BLUNT) |
| `defenseType` | DefenseType (Enum) | NULLABLE | 방어 타입 |
| `skillImage` | String(500) | NULLABLE | 스킬 이미지 URL |

**관계:**
- `persona`: `PersonaJpa` - **N:1** (LAZY, NOT NULL)
- `statsBySync`: `List<SkillStatsBySyncJpa>` - **1:N** (mappedBy = "skill", CASCADE_ALL, orphanRemoval = true)

**양방향 관계 편의 메서드:**
- `setPersona(PersonaJpa persona)`
- `addStatsBySync(SkillStatsBySyncJpa stats)` / `removeStatsBySync(SkillStatsBySyncJpa stats)`

**도메인 매핑:**
- `ofDomain(Skill)`: 도메인 객체 → JPA 엔티티
- `updateFromDomain(Skill)`: 도메인 객체로 기존 엔티티 업데이트

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/SkillJpa.java:1`

---

#### 2.2 SkillStatsBySyncJpa

**테이블명:** `skill_stats_by_sync`

**설명:** 동기화 레벨(Sync Level)별 스킬 스탯 정보

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `syncLevel` | SyncLevel (Enum) | NOT NULL | 동기화 레벨 (SYNC_1, SYNC_2, SYNC_3, SYNC_4) |
| `basePower` | int | NOT NULL | 기본 위력 |
| `coinPower` | int | NOT NULL | 코인 위력 |
| `coinCount` | int | NOT NULL | 코인 개수 |
| `weight` | int | NOT NULL | 가중치 |
| `level` | int | NOT NULL | 레벨 |

**관계:**
- `skill`: `SkillJpa` - **N:1** (LAZY, NOT NULL)
- `skillEffects`: `List<SkillEffectJpa>` - **1:N** (mappedBy = "statsBySync", CASCADE_ALL, orphanRemoval = true)
- `skillCoins`: `List<SkillCoinJpa>` - **1:N** (mappedBy = "statsBySync", CASCADE_ALL, orphanRemoval = true)

**양방향 관계 편의 메서드:**
- `setSkill(SkillJpa skill)`
- `addSkillEffect(SkillEffectJpa effect)` / `removeSkillEffect(SkillEffectJpa effect)`
- `addSkillCoin(SkillCoinJpa coin)` / `removeSkillCoin(SkillCoinJpa coin)`

**도메인 매핑:**
- `ofDomain(SkillStatsBySync)`: 도메인 객체 → JPA 엔티티
- `updateFromDomain(SkillStatsBySync)`: 도메인 객체로 기존 엔티티 업데이트

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/SkillStatsBySyncJpa.java:1`

---

#### 2.3 SkillCoinJpa

**테이블명:** `skill_coin`

**설명:** 스킬의 개별 코인 정보

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `orderIndex` | int | NOT NULL | 코인 순서 (0부터 시작) |
| `coinType` | CoinType (Enum) | NOT NULL | 코인 타입 (POSITIVE, NEGATIVE 등) |

**관계:**
- `statsBySync`: `SkillStatsBySyncJpa` - **N:1** (LAZY, NOT NULL)
- `coinEffects`: `List<CoinEffectJpa>` - **1:N** (mappedBy = "skillCoin", CASCADE_ALL, orphanRemoval = true)

**양방향 관계 편의 메서드:**
- `setStatsBySync(SkillStatsBySyncJpa statsBySync)`
- `addCoinEffect(CoinEffectJpa effect)` / `removeCoinEffect(CoinEffectJpa effect)`

**도메인 매핑:**
- `ofDomain(SkillCoin)`: 도메인 객체 → JPA 엔티티
- `updateFromDomain(SkillCoin)`: 도메인 객체로 기존 엔티티 업데이트

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/SkillCoinJpa.java:1`

---

### 3. 패시브 관련 엔티티

#### 3.1 PersonaPassiveJpa

**테이블명:** `persona_passive`

**설명:** 인격의 패시브 스킬 (일반 패시브 / 서포트 패시브)

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `name` | String(100) | NOT NULL | 패시브 이름 |
| `kind` | PassiveKind (Enum) | NOT NULL | 패시브 종류 (NORMAL, SUPPORT) |
| `syncLevel` | SyncLevel (Enum) | NULLABLE | 동기화 레벨 (SUPPORT일 때만) |

**패시브 조건 (Embedded - PassiveCondition 평탄화):**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `conditionSinAffinity` | SinAffinity (Enum) | NULLABLE | 조건 죄악 속성 |
| `conditionType` | ConditionType (Enum) | NULLABLE | 조건 타입 |
| `conditionCount` | Integer | NULLABLE | 조건 개수 |

**관계:**
- `persona`: `PersonaJpa` - **N:1** (LAZY, NOT NULL)
- `effect`: `PassiveEffectJpa` - **1:1** (mappedBy = "personaPassive", CASCADE_ALL, orphanRemoval = true)

**양방향 관계 편의 메서드:**
- `setPersona(PersonaJpa persona)`
- `setEffect(PassiveEffectJpa effect)`

**도메인 매핑:**
- `ofDomain(PersonaPassive)`: 도메인 객체 → JPA 엔티티 (Optional 처리)
- `updateFromDomain(PersonaPassive)`: 도메인 객체로 기존 엔티티 업데이트

**특징:**
- PassiveCondition을 별도 테이블로 분리하지 않고 **평탄화(Flatten)** 전략 사용
- 조건이 없는 경우 모든 condition 필드가 null

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/PersonaPassiveJpa.java:1`

---

### 4. 효과 시스템 엔티티

효과 시스템은 **3가지 타입의 효과**를 동일한 구조로 처리합니다:
1. **SkillEffectJpa**: 스킬 전체에 적용되는 효과 (예: `[사용시] 충전 10 소모`)
2. **CoinEffectJpa**: 개별 코인에 적용되는 효과 (예: `[적중시] 출혈 2 부여`)
3. **PassiveEffectJpa**: 패시브 효과

모든 효과는 다음 구조를 따릅니다:
```
Effect → trigger → rootCondition → branches[] → actions[]
```

---

#### 4.1 SkillEffectJpa

**테이블명:** `skill_effect`

**설명:** 스킬 전체에 적용되는 효과

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `triggerJson` | String (TEXT) | NOT NULL | 트리거 정보 (JSON 직렬화) |
| `originalText` | String (TEXT) | NULLABLE | 원본 텍스트 (게임 내 설명) |

**관계:**
- `statsBySync`: `SkillStatsBySyncJpa` - **N:1** (LAZY)
- `rootCondition`: `ConditionGroupJpa` - **N:1** (LAZY, CASCADE_ALL) - 루트 조건
- `branches`: `List<EffectBranchJpa>` - **1:N** (@JoinColumn, CASCADE_ALL, orphanRemoval = true, @OrderColumn)

**편의 메서드:**
- `setStatsBySync(SkillStatsBySyncJpa statsBySync)`
- `addBranch(EffectBranchJpa branch)` / `setBranches(List<EffectBranchJpa> branches)`

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/SkillEffectJpa.java:1`

---

#### 4.2 CoinEffectJpa

**테이블명:** `coin_effect`

**설명:** 개별 코인에 적용되는 효과

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `triggerJson` | String (TEXT) | NOT NULL | 트리거 정보 (JSON 직렬화) |
| `originalText` | String (TEXT) | NULLABLE | 원본 텍스트 (게임 내 설명) |

**관계:**
- `skillCoin`: `SkillCoinJpa` - **N:1** (LAZY)
- `rootCondition`: `ConditionGroupJpa` - **N:1** (LAZY, CASCADE_ALL) - 루트 조건
- `branches`: `List<EffectBranchJpa>` - **1:N** (@JoinColumn, CASCADE_ALL, orphanRemoval = true, @OrderColumn)

**편의 메서드:**
- `setSkillCoin(SkillCoinJpa skillCoin)`
- `addBranch(EffectBranchJpa branch)` / `setBranches(List<EffectBranchJpa> branches)`

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/CoinEffectJpa.java:1`

---

#### 4.3 PassiveEffectJpa

**테이블명:** `passive_effect`

**설명:** 패시브 효과

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `triggerJson` | String (TEXT) | NOT NULL | 트리거 정보 (JSON 직렬화) |
| `originalText` | String (TEXT) | NULLABLE | 원본 텍스트 (게임 내 설명) |

**관계:**
- `personaPassive`: `PersonaPassiveJpa` - **1:1** (LAZY)
- `rootCondition`: `ConditionGroupJpa` - **N:1** (LAZY, CASCADE_ALL) - 루트 조건
- `branches`: `List<EffectBranchJpa>` - **1:N** (@JoinColumn, CASCADE_ALL, orphanRemoval = true, @OrderColumn)

**편의 메서드:**
- `setPersonaPassive(PersonaPassiveJpa personaPassive)`
- `addBranch(EffectBranchJpa branch)` / `setBranches(List<EffectBranchJpa> branches)`

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/PassiveEffectJpa.java:1`

---

#### 4.4 EffectBranchJpa

**테이블명:** `effect_branch`

**설명:** 조건부 효과 분기 - 조건이 충족되면 해당 액션들을 실행

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `order` | int | NOT NULL | 분기 순서 |
| `stopOnMatch` | boolean | NOT NULL | 조건 매칭 시 다음 분기 스킵 여부 ("대신" 효과) |
| `originalText` | String (TEXT) | NULLABLE | 원본 텍스트 |

**관계:**
- `condition`: `ConditionGroupJpa` - **N:1** (LAZY, CASCADE_ALL) - 이 분기의 조건
- `actions`: `List<EffectActionJpa>` - **1:N** (mappedBy = "effectBranch", CASCADE_ALL, orphanRemoval = true, @OrderColumn)

**편의 메서드:**
- `addAction(EffectActionJpa action)` / `setActions(List<EffectActionJpa> actions)`

**로직:**
- `condition`이 충족되면 `actions`를 순서대로 실행
- `stopOnMatch = true`면 다음 분기는 실행하지 않음 (if-elseif 동작)

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/EffectBranchJpa.java:1`

---

#### 4.5 EffectActionJpa

**테이블명:** `effect_action`

**설명:** 효과의 실제 동작 (상태 부여, 버프, 위력 증가 등)

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `actionType` | ActionType (Enum) | NOT NULL | 액션 타입 (APPLY_STAT, MODIFY_POWER 등) |
| `statCode` | String | NULLABLE | 스탯 코드 (예: "BLEED", "CHARGE") |
| `scope` | ActionScope (Enum) | NULLABLE | 스코프 (SELF, TARGET, ALL_ENEMIES 등) |
| `policy` | ApplyPolicy (Enum) | NULLABLE | 적용 정책 (ADD, SET, MULTIPLY 등) |
| `amountJson` | String (TEXT) | NULLABLE | 수량 표현식 (JSON 직렬화) |
| `timing` | ActionTiming (Enum) | NULLABLE | 타이밍 (INSTANT, END_OF_TURN 등) |
| `durationTurns` | Integer | NULLABLE | 지속 턴 수 |
| `capMax` | Integer | NULLABLE | 최대 캡 |
| `capPerTarget` | Integer | NULLABLE | 타겟당 최대 캡 |
| `priority` | int | NOT NULL | 우선순위 |
| `targetSelectorJson` | String (TEXT) | NULLABLE | 타겟 선택자 (JSON 직렬화) |
| `coinSelectorJson` | String (TEXT) | NULLABLE | 코인 선택자 (JSON 직렬화) |

**관계:**
- `effectBranch`: `EffectBranchJpa` - **N:1** (LAZY)

**편의 메서드:**
- `setEffectBranch(EffectBranchJpa effectBranch)`

**특징:**
- 복잡한 객체는 JSON으로 직렬화하여 저장 (`amountJson`, `targetSelectorJson`, `coinSelectorJson`)
- 액션 실행 시 역직렬화하여 사용

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/EffectActionJpa.java:1`

---

### 5. 조건 시스템 엔티티

조건 시스템은 **JOINED 상속 전략**을 사용하여 재귀적인 조건 트리를 구현합니다.

```
AbstractConditionJpa (추상 클래스)
├── ConditionGroupJpa (복합 조건 - AND/OR)
│   └── children: AbstractConditionJpa[] (재귀)
├── StatConditionJpa (스탯 조건 - 리프)
└── RangeConditionJpa (범위 조건 - 리프)
```

---

#### 5.1 AbstractConditionJpa

**테이블명:** `abstract_condition`

**설명:** 모든 조건 엔티티의 추상 부모 클래스

**상속 전략:** `InheritanceType.JOINED`

**Discriminator 컬럼:** `condition_type` (STRING)

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | Long | PK, AUTO_INCREMENT | 기본 키 |
| `scope` | ConditionScope (Enum) | NOT NULL | 조건 스코프 (SELF, TARGET, ANY_ALLY 등) |

**자식 클래스:**
- `ConditionGroupJpa` (Discriminator = "GROUP")
- `StatConditionJpa` (Discriminator = "STAT")
- `RangeConditionJpa` (Discriminator = "RANGE")

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/AbstractConditionJpa.java:1`

---

#### 5.2 ConditionGroupJpa

**테이블명:** `condition_group` (JOIN with `abstract_condition`)

**Discriminator:** `"GROUP"`

**설명:** 복합 조건 - 여러 조건을 AND/OR로 결합

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `operator` | LogicalOperator (Enum) | NOT NULL | 논리 연산자 (AND, OR) |
| `ordered` | boolean | NOT NULL | 순서 보존 여부 |

**관계:**
- `children`: `List<AbstractConditionJpa>` - **1:N** (@JoinColumn, CASCADE_ALL, orphanRemoval = true, @OrderColumn)
  - **재귀적 관계**: children도 AbstractConditionJpa 타입이므로 트리 구조 형성

**편의 메서드:**
- `addChild(AbstractConditionJpa child)` / `setChildren(List<AbstractConditionJpa> children)`

**예시:**
```
ConditionGroup (AND)
├── StatCondition (SELF.CHARGE >= 5)
└── ConditionGroup (OR)
    ├── StatCondition (SELF.HP > 50)
    └── RangeCondition (3 <= TARGET.BLEED < 10)
```

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/ConditionGroupJpa.java:1`

---

#### 5.3 StatConditionJpa

**테이블명:** `stat_condition` (JOIN with `abstract_condition`)

**Discriminator:** `"STAT"`

**설명:** 스탯 조건 - 특정 스탯에 대한 비교 조건 (리프 노드)

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `target` | ConditionTarget (Enum) | NOT NULL | 타겟 (SELF, TARGET, ENEMY 등) |
| `statCode` | String | NOT NULL | 스탯 코드 (예: "CHARGE", "HP", "BLEED") |
| `operator` | ConditionOperator (Enum) | NOT NULL | 비교 연산자 (EQ, NE, GT, GTE, LT, LTE) |
| `threshold` | int | NOT NULL | 임계값 |

**예시:**
- `SELF.CHARGE >= 5`: target=SELF, statCode="CHARGE", operator=GTE, threshold=5
- `TARGET.BLEED > 0`: target=TARGET, statCode="BLEED", operator=GT, threshold=0

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/StatConditionJpa.java:1`

---

#### 5.4 RangeConditionJpa

**테이블명:** `range_condition` (JOIN with `abstract_condition`)

**Discriminator:** `"RANGE"`

**설명:** 범위 조건 - 특정 스탯이 범위 내에 있는지 검사 (리프 노드)

**필드:**

| 필드명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `target` | ConditionTarget (Enum) | NOT NULL | 타겟 (SELF, TARGET, ENEMY 등) |
| `statCode` | String | NOT NULL | 스탯 코드 (예: "HP", "SPEED") |
| `minInclusive` | int | NOT NULL | 최소값 (포함) |
| `maxExclusive` | Integer | NULLABLE | 최대값 (미포함, null이면 무한대) |

**예시:**
- `5 <= SELF.HP < 10`: target=SELF, statCode="HP", minInclusive=5, maxExclusive=10
- `3 <= TARGET.BLEED`: target=TARGET, statCode="BLEED", minInclusive=3, maxExclusive=null

**파일 위치:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/RangeConditionJpa.java:1`

---

## 주요 설계 패턴

### 1. Aggregate Pattern

**PersonaJpa**가 **Aggregate Root** 역할을 수행합니다
- Persona를 통해서만 Skill, Passive, Image 접근
- 모든 하위 엔티티는 `CASCADE_ALL`, `orphanRemoval = true`로 생명주기 관리
- 양방향 관계 편의 메서드로 일관성 보장

### 2. JOINED 상속 전략

**AbstractConditionJpa** 계층에서 사용
- 장점: 정규화된 구조, 다형성 쿼리 지원
- 단점: 조인 오버헤드 (조건 트리가 깊어질 경우)
- 선택 이유: 타입별 컬럼이 다르고 재귀적 참조가 필요하여 JOINED가 적합

### 3. 임베디드 평탄화

**PersonaJpa**의 Resistance, Speed, Health, Season 정보
- Value Object를 별도 테이블로 분리하지 않고 **평탄화(Flatten)**
- 장점: 조회 성능 향상, 단순한 구조
- 단점: 컬럼 수 증가

**PersonaPassiveJpa**의 PassiveCondition
- Optional<PassiveCondition>을 3개의 nullable 컬럼으로 평탄화
- 조건이 없는 경우 모두 null

### 4. JSON 직렬화 전략

복잡한 객체는 JSON으로 직렬화하여 TEXT 컬럼에 저장
- `triggerJson` (SkillTrigger)
- `amountJson` (AmountExpression)
- `targetSelectorJson` (TargetSelector)
- `coinSelectorJson` (CoinSelector)

**장점:**
- 유연한 데이터 구조
- 테이블 수 감소

**단점:**
- 쿼리 불가능
- 직렬화/역직렬화 오버헤드

**적용 기준:** 읽기 전용이거나 복잡한 계산 로직에만 사용되는 데이터

### 5. 양방향 관계 편의 메서드

모든 부모-자식 관계에 **편의 메서드** 제공
```java
public void addSkill(SkillJpa skill) {
    this.skills.add(skill);
    skill.setPersona(this);  // 역방향 설정
}

public void removeSkill(SkillJpa skill) {
    this.skills.remove(skill);
    skill.setPersona(null);  // 역방향 해제
}
```

**효과:** 양방향 관계 일관성 보장, 개발자 실수 방지

### 6. 도메인 변환 메서드

모든 엔티티에 도메인 객체 변환 메서드 제공
- `ofDomain(Domain domain)`: 도메인 → JPA 엔티티 (정적 팩토리 메서드)
- `updateFromDomain(Domain domain)`: 기존 엔티티를 도메인 값으로 업데이트

**목적:** 헥사고날 아키텍처에서 도메인과 인프라 계층 분리

---

## 데이터베이스 매핑 전략

### 테이블 목록

| 테이블명 | 엔티티 | 설명 |
|----------|--------|------|
| `sinner` | SinnerJpa | 시너 (12명) |
| `persona` | PersonaJpa | 인격 |
| `persona_image` | PersonaImageJpa | 인격 이미지 |
| `persona_passive` | PersonaPassiveJpa | 패시브 스킬 |
| `passive_effect` | PassiveEffectJpa | 패시브 효과 |
| `skill` | SkillJpa | 스킬 |
| `skill_stats_by_sync` | SkillStatsBySyncJpa | 동기화 레벨별 스킬 스탯 |
| `skill_coin` | SkillCoinJpa | 스킬 코인 |
| `skill_effect` | SkillEffectJpa | 스킬 효과 |
| `coin_effect` | CoinEffectJpa | 코인 효과 |
| `effect_branch` | EffectBranchJpa | 효과 분기 |
| `effect_action` | EffectActionJpa | 효과 액션 |
| `abstract_condition` | AbstractConditionJpa | 조건 (부모 테이블) |
| `condition_group` | ConditionGroupJpa | 복합 조건 (JOIN) |
| `stat_condition` | StatConditionJpa | 스탯 조건 (JOIN) |
| `range_condition` | RangeConditionJpa | 범위 조건 (JOIN) |

**총 테이블 수:** 16개

### 외래 키 관계

```
sinner
└── persona (sinner_id → sinner.id)
    ├── persona_image (persona_id → persona.id)
    ├── persona_passive (persona_id → persona.id)
    │   └── passive_effect (persona_passive_id → persona_passive.id)
    │       ├── (root_condition_id → condition_group.id)
    │       └── effect_branch (passive_effect_id → passive_effect.id)
    │           └── effect_action (effect_branch_id → effect_branch.id)
    └── skill (persona_id → persona.id)
        └── skill_stats_by_sync (skill_id → skill.id)
            ├── skill_effect (stats_by_sync_id → skill_stats_by_sync.id)
            │   ├── (root_condition_id → condition_group.id)
            │   └── effect_branch (skill_effect_id → skill_effect.id)
            │       └── effect_action (effect_branch_id → effect_branch.id)
            └── skill_coin (stats_by_sync_id → skill_stats_by_sync.id)
                └── coin_effect (skill_coin_id → skill_coin.id)
                    ├── (root_condition_id → condition_group.id)
                    └── effect_branch (coin_effect_id → coin_effect.id)
                        └── effect_action (effect_branch_id → effect_branch.id)

condition_group (id → abstract_condition.id)
├── (parent_group_id → condition_group.id) -- 재귀
└── children: abstract_condition[]

stat_condition (id → abstract_condition.id)
range_condition (id → abstract_condition.id)
```

### Cascade 전략

모든 **부모 → 자식** 관계는 `CascadeType.ALL` + `orphanRemoval = true` 사용
- Persona를 삭제하면 관련 Skill, Passive, Image 모두 삭제
- SkillStatsBySync를 삭제하면 관련 SkillEffect, SkillCoin 모두 삭제
- Effect를 삭제하면 관련 Branch, Action 모두 삭제

**주의사항:** Cascade는 단방향으로만 전파되므로 Aggregate Root를 통해서만 삭제 수행

### Fetch 전략

- 모든 `@ManyToOne`, `@OneToOne`: **LAZY** (기본값 또는 명시)
- 모든 `@OneToMany`: **LAZY** (기본값)

**이유:** N+1 문제 방지, Fetch Join으로 필요 시 최적화

### 인덱스 권장사항

- `sinner.name`, `sinner.name_en`: UNIQUE 제약조건
- `persona.sinner_id`: 외래 키 인덱스 (자동 생성)
- `skill.persona_id`, `skill.skill_number`: 복합 인덱스 고려
- `skill_stats_by_sync.skill_id`, `skill_stats_by_sync.sync_level`: 복합 인덱스 고려
- `abstract_condition.condition_type`: Discriminator 인덱스 (JOINED 전략)

---

## 요약

### 엔티티 통계
- **총 엔티티 수:** 16개
- **Aggregate Root:** PersonaJpa
- **상속 계층:** AbstractConditionJpa (JOINED)
- **임베디드 평탄화:** PersonaJpa (Resistance, Speed, Health, Season), PersonaPassiveJpa (PassiveCondition)
- **JSON 직렬화 필드:** 5개 (triggerJson, amountJson, targetSelectorJson, coinSelectorJson)

### 주요 특징
1. **명확한 계층 구조**: Sinner → Persona → Skill/Passive → Effect → Branch → Action
2. **재귀적 조건 트리**: JOINED 상속 + AbstractConditionJpa
3. **효과 시스템 통합**: SkillEffect, CoinEffect, PassiveEffect가 동일한 구조 공유
4. **헥사고날 아키텍처**: 도메인 ↔ JPA 변환 메서드 제공
5. **Cascade 생명주기 관리**: Aggregate Root 중심의 일관된 삭제 전파

### 모듈 위치
- **패키지:** `org.yyubin.gesellschaftinfrastructure.jpa`
- **모듈:** `gesellschaft-infrastructure`
- **소스 경로:** `gesellschaft-infrastructure/src/main/java/org/yyubin/gesellschaftinfrastructure/jpa/`

---

**문서 작성일:** 2025-11-06

**문서 수정일:** 2025-11-06
