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
5. [Enum 타입 정의](#enum-타입-정의)
   - [코어 Enum](#1-코어-enum)
   - [스킬 관련 Enum](#2-스킬-관련-enum)
   - [효과 시스템 Enum](#3-효과-시스템-enum)
   - [조건 시스템 Enum](#4-조건-시스템-enum)
   - [패시브 관련 Enum](#5-패시브-관련-enum)
6. [주요 설계 패턴](#주요-설계-패턴)
7. [데이터베이스 매핑 전략](#데이터베이스-매핑-전략)

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

## Enum 타입 정의

이 섹션은 `gesellschaft-domain/src/main/java/model` 패키지에 정의된 모든 Enum 타입을 설명합니다.

### 1. 코어 Enum

#### 1.1 GradeType

**설명:** 인격의 등급 (희귀도)

**패키지:** `model`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `ONE` | 1성 인격 |
| `TWO` | 2성 인격 |
| `THREE` | 3성 인격 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/GradeType.java:1`

---

#### 1.2 AttackType

**설명:** 공격 타입 (피해 속성)

**패키지:** `model`

**값:**

| Enum 값 | 설명 (한글) |
|---------|-------------|
| `SLASH` | 참격 |
| `PIERCE` | 관통 |
| `BLUNT` | 타격 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/AttackType.java:1`

---

#### 1.3 SinAffinity

**설명:** 죄악 속성 (림버스 컴퍼니의 7가지 대죄)

**패키지:** `model`

**값:**

| Enum 값 | 한글명 (`nameKo`) |
|---------|-------------------|
| `WRATH` | 분노 |
| `LUST` | 색욕 |
| `SLOTH` | 나태 |
| `GREED` | 탐식 |
| `GLOOM` | 우울 |
| `PRIDE` | 오만 |
| `ENVY` | 질투 |
| `NONE` | 없음 |

**특징:**
- `@Getter` 어노테이션으로 `nameKo` 필드 접근 가능
- 게임 내 죄악 공명 시스템에 사용

**파일 위치:** `gesellschaft-domain/src/main/java/model/SinAffinity.java:1`

---

#### 1.4 KeywordType

**설명:** 스킬 키워드 타입 (상태 이상 속성)

**패키지:** `model`

**값:**

| Enum 값 | 한글명 (`nameKo`) |
|---------|-------------------|
| `BURN` | 화상 |
| `BLEED` | 출혈 |
| `TREMOR` | 진동 |
| `RUPTURE` | 파열 |
| `SINKING` | 침잠 |
| `BREATH` | 호흡 |
| `CHARGE` | 충전 |
| `NONE` | 없음 |

**특징:**
- `@Getter` 어노테이션으로 `nameKo` 필드 접근 가능
- 스킬이 주로 다루는 상태 이상 종류를 나타냄

**파일 위치:** `gesellschaft-domain/src/main/java/model/KeywordType.java:1`

---

#### 1.5 ResistanceType

**설명:** 내성 타입 (공격 타입에 대한 저항력)

**패키지:** `model.persona`

**값:**

| Enum 값 | 설명 (한글) |
|---------|-------------|
| `NORMAL` | 보통 (1.0배) |
| `WEAK` | 취약 (더 많은 피해) |
| `RESIST` | 내성 (적은 피해) |

**파일 위치:** `gesellschaft-domain/src/main/java/model/persona/ResistanceType.java:1`

---

#### 1.6 SeasonType

**설명:** 시즌 타입 (인격 출시 시즌 분류)

**패키지:** `model.persona`

**값:**

| Enum 값 | 표시명 (`displayName`) |
|---------|------------------------|
| `NORMAL` | 통상 |
| `SEASON_NORMAL` | 시즌 |
| `SEASON_EVENT` | 시즌 이벤트 |
| `WALPURGISNACHT` | 발푸르기스 |

**특징:**
- `@Getter` 어노테이션으로 `displayName` 필드 접근 가능
- 인격의 출시 시기 및 획득 방법 분류

**파일 위치:** `gesellschaft-domain/src/main/java/model/persona/SeasonType.java:1`

---

#### 1.7 ImageType

**설명:** 인격 이미지 타입

**패키지:** `model.persona`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `A` | 기본 일러스트 |
| `B` | 대체 일러스트 |
| `AC` | 기본 코어파트 |
| `BC` | 대체 코어파트 |
| `SD` | SD 일러스트 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/persona/ImageType.java:1`

---

### 2. 스킬 관련 Enum

#### 2.1 SkillCategoryType

**설명:** 스킬 카테고리 (공격/방어)

**패키지:** `model.skill`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `ATTACK` | 공격 스킬 |
| `DEFENSE` | 방어 스킬 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/SkillCategoryType.java:1`

---

#### 2.2 DefenseType

**설명:** 방어 스킬 타입

**패키지:** `model.skill`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `COUNTER` | 반격 |
| `EVADE` | 회피 |
| `GUARD` | 방어 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/DefenseType.java:1`

---

#### 2.3 SyncLevel

**설명:** 동기화 레벨 (인격 강화 단계)

**패키지:** `model.skill`

**값:**

| Enum 값 | 레벨 (`level`) |
|---------|----------------|
| `SYNC_1` | 1 |
| `SYNC_2` | 2 |
| `SYNC_3` | 3 |
| `SYNC_4` | 4 |

**메서드:**
- `int getLevel()`: 레벨 숫자 반환
- `static SyncLevel fromInt(int level)`: 숫자로부터 SyncLevel 생성

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/SyncLevel.java:1`

---

#### 2.4 CoinType

**설명:** 스킬 코인 타입

**패키지:** `model.skill`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `NORMAL` | 일반 코인 |
| `UNBREAKABLE` | 불파괴 코인 (합 패배 시에도 파괴 안됨) |
| `REUSE` | 재사용 코인 (특수) |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/CoinType.java:1`

---

#### 2.5 TriggerCode

**설명:** 효과 발동 트리거 (스킬 및 패시브)

**패키지:** `model.skill`

**스킬 트리거:**

| Enum 값 | 설명 |
|---------|------|
| `NONE` | 트리거 없음 |
| `ON_HIT` | [적중시] |
| `ON_CRITICAL_HIT` | [크리티컬 적중시] |
| `ON_HEAD_HIT` | [앞면 적중시] |
| `ON_TAIL_HIT` | [뒷면 적중시] |
| `ON_USE` | [사용시] |
| `ON_WIN_CLASH` | [합 승리시] |
| `ON_WIN_CLASH_HIT` | [합 승리 적중시] |
| `ON_LOSE_CLASH` | [합 패배시] |
| `ON_DROP` | [이 스킬이 버려지면] |
| `ON_ATTACK_END` | [공격 종료 시] |
| `ON_KILL` | [적 처치 시] |

**패시브 트리거:**

| Enum 값 | 설명 |
|---------|------|
| `ON_BATTLE_START` | [전투 시작 시] |
| `ON_TURN_START` | [턴 시작 시] |
| `ON_TURN_END` | [턴 종료 시] |
| `ON_ALLY_ATTACK` | [아군이 공격을 가할 시] |
| `ON_ALLY_HIT` | [아군이 적중 시] |
| `ON_ALLY_KILL` | [아군이 적 처치 시] |
| `ON_DAMAGED` | [피격 시] |
| `ON_STATUS_INFLICTED` | [상태 이상 부여 시] |
| `ON_STATUS_RECEIVED` | [상태 이상 받을 시] |
| `ALWAYS` | [전투 중] (항상 활성) |

**기타:**

| Enum 값 | 설명 |
|---------|------|
| `ON_CONDITION_MET` | 명시된 조건 충족 시 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/TriggerCode.java:1`

---

### 3. 효과 시스템 Enum

#### 3.1 ActionType

**설명:** 효과 동작 타입 (실제 게임 내 효과)

**패키지:** `model.skill.effect`

**상태 이상:**

| Enum 값 | 설명 |
|---------|------|
| `STATUS_INFLICT` | 상태 이상 부여 |
| `STATUS_REMOVE` | 상태 해제 |

**스탯 버프/디버프:**

| Enum 값 | 설명 |
|---------|------|
| `BUFF_DAMAGE_UP` | 공격력 증가 |
| `BUFF_DAMAGE_DOWN` | 공격력 감소 |
| `BUFF_DEFENSE_UP` | 방어력 증가 |
| `BUFF_DEFENSE_DOWN` | 방어력 감소 |

**리소스:**

| Enum 값 | 설명 |
|---------|------|
| `RESOURCE_GAIN` | 자원 획득 (충전, 경혈 등) |
| `RESOURCE_CONSUME` | 자원 소모 |
| `RESOURCE_SET` | 자원 설정 (고정값으로) |

**피해/회복:**

| Enum 값 | 설명 |
|---------|------|
| `DAMAGE_MODIFY` | 피해량 변경 (%) |
| `POWER_MODIFY` | 위력 변경 |
| `HEAL_HP` | 체력 회복 |
| `CONSUME_HP` | 체력 소모 |

**코인:**

| Enum 값 | 설명 |
|---------|------|
| `COIN_POWER_UP` | 코인 위력 + |
| `CLASH_POWER_UP` | 합 위력 + |

**특수:**

| Enum 값 | 설명 |
|---------|------|
| `COMMAND_ATTACK` | 원호 공격 명령 |
| `TRANSFORM_SKILL` | 스킬 변환 |
| `SUPPRESS_EFFECT` | 기존 효과 억제 |
| `ETC` | 기타 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/effect/ActionType.java:1`

---

#### 3.2 ActionScope

**설명:** 효과 적용 범위 (스킬/코인)

**패키지:** `model.skill.effect`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `SKILL` | 스킬 전체 |
| `COINS_SKILL` | 스킬의 모든 코인 |
| `COIN_EACH` | 각 코인마다 |
| `COIN_LAST` | 마지막 코인 |
| `COIN_FIRST` | 첫 번째 코인 |
| `COIN_INDEX` | 특정 인덱스 코인 |
| `THIS_COIN` | 현재 코인 (코인 효과에서) |
| `NEXT_COIN` | 다음 코인 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/effect/ActionScope.java:1`

---

#### 3.3 ApplyPolicy

**설명:** 효과 적용 정책

**패키지:** `model.skill.effect`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `ADD` | 덧셈 (기존 값에 추가) |
| `MULTIPLY` | 곱셈 |
| `OVERRIDE` | 덮어쓰기 |
| `SUPPRESS` | 기존 효과 억제 |
| `REPLACE_OUTCOME` | 결과 대체 |
| `SET` | 고정값 설정 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/effect/ApplyPolicy.java:1`

---

#### 3.4 AmountExprType

**설명:** 수치 표현 방식

**패키지:** `model.skill.effect`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `FLAT` | 고정 숫자 (예: "3") |
| `FORMULA` | 동적 계산식 (예: "충전 / 5") |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/effect/AmountExprType.java:1`

---

#### 3.5 ActionUnit

**설명:** 수치 단위

**패키지:** `model.skill.effect`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `STACK` | 스택 (상태 이상) |
| `FLAT` | 절대값 |
| `PERCENT` | 백분율 (%) |
| `PERCENT_OF_MAX_HP` | 최대 체력의 % |
| `PERCENT_OF_DAMAGE` | 입힌 피해의 % |
| `PER_N` | N당 (설정값 필요) |
| `PER_3` | 3당 |
| `PER_10` | 10당 |
| `UNITLESS` | 단위 없음 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/effect/ActionUnit.java:1`

---

#### 3.6 ActionTiming

**설명:** 효과 적용 시점

**패키지:** `model.skill.effect`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `IMMEDIATE` | 즉시 |
| `THIS_TURN` | 이번 턴 |
| `NEXT_TURN` | 다음 턴 |
| `TURN_END` | 턴 종료 시 |
| `NEXT_COIN` | 다음 코인 |
| `ATTACK_END` | 공격 종료 시 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/effect/ActionTiming.java:1`

---

#### 3.7 SelectorType

**설명:** 대상 선택 전략

**패키지:** `model.skill.effect`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `ALL` | 모두 |
| `RANDOM` | 랜덤 |
| `HIGHEST` | 특정 스탯 최대 |
| `LOWEST` | 특정 스탯 최소 |
| `FASTEST` | 속도 가장 빠른 |
| `SLOWEST` | 속도 가장 느린 |
| `FIRST` | 첫 번째 |
| `LAST` | 마지막 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/effect/SelectorType.java:1`

---

#### 3.8 CoinSelectorType

**설명:** 코인 선택 타입

**패키지:** `model.skill.effect`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `ALL` | 모든 코인 |
| `FIRST` | 첫 번째 코인 |
| `LAST` | 마지막 코인 |
| `INDEX` | 특정 인덱스 |
| `CURRENT` | 현재 코인 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/effect/CoinSelectorType.java:1`

---

### 4. 조건 시스템 Enum

#### 4.1 ConditionScope

**설명:** 조건 범위 (어느 단계의 컨텍스트인지)

**패키지:** `model.skill`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `BATTLE` | 전투 레벨 |
| `TURN` | 턴 레벨 |
| `SKILL` | 스킬 레벨 |
| `COIN` | 코인 레벨 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/ConditionScope.java:1`

---

#### 4.2 LogicalOperator

**설명:** 논리 연산자 (복합 조건)

**패키지:** `model.skill`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `AND` | 논리곱 (모든 조건 만족) |
| `OR` | 논리합 (하나 이상 만족) |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/LogicalOperator.java:1`

---

#### 4.3 ConditionOperator

**설명:** 조건 비교 연산자

**패키지:** `model.skill`

**값:**

| Enum 값 | 설명 | 기호 |
|---------|------|------|
| `EQUAL` | 같음 | `==` |
| `NOT_EQUAL` | 같지 않음 | `!=` |
| `GREATER_THAN` | 초과 | `>` |
| `GREATER_THAN_OR_EQUAL` | 이상 | `>=` |
| `LESS_THAN` | 미만 | `<` |
| `LESS_THAN_OR_EQUAL` | 이하 | `<=` |
| `IN_RANGE` | 범위 내 | `BETWEEN` |
| `DIVISIBLE_BY` | 나누어떨어짐 | `% N == 0` (N당) |
| `HAS_TAG` | 태그 보유 여부 | - |
| `HAS_STATUS` | 상태 이상 보유 여부 | - |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/ConditionOperator.java:1`

---

#### 4.4 ConditionTarget

**설명:** 조건 검사 대상

**패키지:** `model.skill`

**기본 대상:**

| Enum 값 | 설명 |
|---------|------|
| `SELF` | 자신 |
| `ENEMY` | 대상 (단일 적) |
| `ENEMY_ALL` | 모든 적 |
| `ALLY` | 아군 (단일) |
| `ALLY_ALL` | 모든 아군 |
| `SELF_ALLY` | 자신 포함 아군 |
| `ANY` | 모두 |

**조작 패널 위치 기반:**

| Enum 값 | 설명 |
|---------|------|
| `RIGHT_ALLY` | 우측 아군 |
| `LEFT_ALLY` | 좌측 아군 |

**특수:**

| Enum 값 | 설명 |
|---------|------|
| `LOWEST_HP_ALLY` | 체력 비율 낮은 아군 |
| `HIGHEST_RESONANCE` | 공명 높은 대상 |

**파일 위치:** `gesellschaft-domain/src/main/java/model/skill/ConditionTarget.java:1`

---

### 5. 패시브 관련 Enum

#### 5.1 PassiveKind

**설명:** 패시브 종류

**패키지:** `model.passive`

**값:**

| Enum 값 | 설명 |
|---------|------|
| `NORMAL` | 일반 패시브 (기본) |
| `SUPPORT` | 서포트 패시브 (동기화 3/4) |

**파일 위치:** `gesellschaft-domain/src/main/java/model/passive/PassiveKind.java:1`

---

#### 5.2 ConditionType (패시브 조건)

**설명:** 패시브 발동 조건 타입

**패키지:** `model.passive`

**값:**

| Enum 값 | 한글명 (`nameKo`) | 예시 |
|---------|-------------------|------|
| `HOLD` | 보유 | "오만 보유 3" |
| `RESONATE` | 공명 | "오만 공명 3" |

**특징:**
- `@Getter` 어노테이션으로 `nameKo` 필드 접근 가능
- 패시브 스킬 발동을 위한 죄악 속성 조건

**파일 위치:** `gesellschaft-domain/src/main/java/model/passive/ConditionType.java:1`

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
- **총 Enum 수:** 26개
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
