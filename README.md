# 게젤샤프트 (Gesellschaft)

**림버스 컴퍼니 데이터/로직 아카이브 & 서비스**

> 목표: 캐릭터/인격/동기화/스킬 메타데이터를 명확하게 구조화하고, 헥사고날 아키텍처로 읽기 중심의 공개 API와 관리용 백오피스를 제공. 추후 DSL과 추천(조합 제안) 시스템으로 확장.

---

## 1) 프로젝트 개요

* 대상 게임: **림버스 컴퍼니**
* 기술 스택: **Spring Boot**, **JPA**, **MySQL**, **Jinx**(DDL 생성)
* 범위(초기): **회원 기능 없음** (읽기 전용 서비스 + 어드민 입력/수정 전용)
* 아키텍처: **헥사고날(Ports & Adapters)**
* 장기 계획: **게임 전용 DSL** 설계 → **조합 추천/시뮬레이터** 추가

---

## 2) 도메인 정의 (초안)

### 핵심 개념

* **Sinner(시너)**: 고정 12명.
* **Persona(인격)**: 각 Sinner가 여러 개의 인격을 가짐.
* **Sync(동기화) 레벨**: 인격별 1~4 단계. 레벨별로 스킬이 달라질 수 있음.
* **Skill(스킬)**: 인격(또는 특정 동기화 레벨)에 종속되는 전투 기술.

    * 속성: **죄악 속성(SinAffinity)**, **타입(AttackType: 참격/관통/타격)**, **코인(Coin)** 등.
    * 추가 메타(추후 확정): 기본 위력, 코인 수/가중치, 적중/치명, 버프/디버프 태그, 조건부 효과 등.

### 용어(초안)

* **SinAffinity**: Wrath, Lust, Sloth, Gluttony, Gloom, Pride, Envy (확정 필요)
* **AttackType**: Slash(참격), Pierce(관통), Blunt(타격)
* **Coin**: 스킬의 코인 매커니즘 관련 세부정보(코인 수/증가량/표기 규칙 등, 상세 스펙 협의)

### 경계/집합체 설계(초안)

* **Aggregate: Sinner**

    * id, name, (기본 프로필)
    * children: Persona[*]
* **Aggregate: Persona**

    * id, sinnerId, name, rarity, releaseSeason 등
    * children: PersonaSync[*]
* **Entity: PersonaSync**

    * id, personaId, level(1..4)
    * children: Skill[*]
* **Entity: Skill**

    * id, personaSyncId (또는 personaId + level), name
    * sinAffinity, attackType, coins[*], basePower, tags[*], effectTexts[*]
* **Value Objects**

    * CoinSpec(count, step, min/max?)
    * SkillTag(name), EffectText(locale, text)

> 초기 모델은 **읽기 일관성**과 **관리 편의성**을 우선. 실제 게임 룰 차이를 반영해 **동기화 레벨별 스킬 차등**을 자연스럽게 담기 위해 `PersonaSync` 하위에 `Skill`을 둠.

---

## 3) 헥사고날 아키텍처

### 모듈 구성(멀티모듈 권장)

```
/gesellschaft
  ├─ gesellschaft-domain        # 순수 도메인 모델 + 도메인 서비스 + 포트 인터페이스
  ├─ gesellschaft-application   # 유스케이스(커맨드/쿼리) + DTO + 서비스 조합
  ├─ gesellschaft-infrastructure# JPA 어댑터, Repository 구현, DB 설정, Jinx 연동
  └─ gesellschaft-boot          # Spring Boot API/관리 콘솔, Web/REST 어댑터
```

### 포트 & 어댑터(예시)

* **Inbound Ports (Use Case)**

    * `QuerySinnerPort`: 시너/인격/스킬 조회
    * `SearchPersonaPort`: 조건 검색(속성/타입/태그 등)
    * `ManageCatalogPort`(어드민 한정): 메타데이터 CRUD, 릴리즈 관리
* **Outbound Ports**

    * `PersonaRepositoryPort`: 도메인 엔티티 영속화(읽기 최적화 포함)
    * `SkillSearchPort`: 전문 검색(선택: 추후 ES 연동 고려)
    * `MigrationPort`: Jinx 기반 스키마 마이그레이션 훅
* **Adapters**

    * Inbound: REST 컨트롤러(`public-api`, `admin-api`), Batch(importer)
    * Outbound: JPA(repository), Jinx(migration), (선택) Cache(Redis)

### API 설계(초안)

* Public(Read-Only)

    * `GET /api/sinners` → 12명 목록
    * `GET /api/sinners/{id}` → 시너 상세(인격 요약 포함)
    * `GET /api/personas/{id}` → 인격 상세(동기화 레벨/스킬 포함)
    * `GET /api/skills?sin=PRIDE&type=SLASH&tag=bleed` → 조건 검색
* Admin(Protected but **회원 비활성**이므로 나중에 key/token 기반 단독 보호)

    * `POST /admin/personas` / `PATCH /admin/skills/{id}` 등

---

## 4) 데이터 모델(초안 ERD)

```
SINNER(id PK, code UNIQUE, name, ...)
PERSONA(id PK, sinner_id FK→SINNER, code UNIQUE, name, rarity, release_season, ...)
PERSONA_SYNC(id PK, persona_id FK→PERSONA, level INT CHECK 1..4)
SKILL(id PK, persona_sync_id FK→PERSONA_SYNC, code UNIQUE, name,
      sin_affinity ENUM, attack_type ENUM, base_power INT, ...)
SKILL_COIN(id PK, skill_id FK→SKILL, ord INT, count INT, step INT, ...)
SKILL_TAG(skill_id FK→SKILL, tag VARCHAR)
SKILL_EFFECT_TEXT(skill_id FK→SKILL, locale VARCHAR(5), text TEXT)
```

> `code` 컬럼은 자연키/외부 연동 및 안정적 참조를 위해 권장. 사용자 Facing URL에도 활용 가능.

---

## 5) JPA 매핑(샘플)

```java
// domain
public enum SinAffinity { WRATH, LUST, SLOTH, GLUTTONY, GLOOM, PRIDE, ENVY }
public enum AttackType { SLASH, PIERCE, BLUNT }
```

```java
// infrastructure (JPA)
@Entity
class SinnerJpa {
  @Id @GeneratedValue Long id;
  @Column(nullable=false, unique=true) String code;
  String name;
  @OneToMany(mappedBy="sinner", cascade=ALL, orphanRemoval=true)
  List<PersonaJpa> personas = new ArrayList<>();
}

@Entity
class PersonaJpa {
  @Id @GeneratedValue Long id;
  @ManyToOne(fetch=LAZY) SinnerJpa sinner;
  @Column(nullable=false, unique=true) String code;
  String name; String rarity; String releaseSeason;
  @OneToMany(mappedBy="persona", cascade=ALL, orphanRemoval=true)
  List<PersonaSyncJpa> syncs = new ArrayList<>();
}

@Entity
class PersonaSyncJpa {
  @Id @GeneratedValue Long id;
  @ManyToOne(fetch=LAZY) PersonaJpa persona;
  @Column(nullable=false) int level; // 1..4 검증
  @OneToMany(mappedBy="sync", cascade=ALL, orphanRemoval=true)
  List<SkillJpa> skills = new ArrayList<>();
}

@Entity
class SkillJpa {
  @Id @GeneratedValue Long id;
  @ManyToOne(fetch=LAZY) PersonaSyncJpa sync;
  @Column(nullable=false, unique=true) String code;
  String name;
  @Enumerated(EnumType.STRING) SinAffinity sinAffinity;
  @Enumerated(EnumType.STRING) AttackType attackType;
  Integer basePower;
  @OneToMany(mappedBy="skill", cascade=ALL, orphanRemoval=true)
  List<SkillCoinJpa> coins = new ArrayList<>();
}
```

> 도메인 모델은 `gesellschaft-domain`에 **순수 클래스/VO**로 유지하고, 위 JPA 엔티티는 `gesellschaft-infrastructure`에서 **매핑용 어댑터**로만 사용.

---

## 6) 패키지 & 계층 구조

```
…/gesellschaft-domain
  ├─ model
  │   ├─ Sinner, Persona, PersonaSync, Skill, CoinSpec, …
  │   └─ enums: SinAffinity, AttackType, …
  ├─ port
  │   ├─ in : QuerySinnerUseCase, SearchPersonaUseCase, ManageCatalogUseCase
  │   └─ out: PersonaRepositoryPort, SkillSearchPort, MigrationPort
  └─ service (도메인 서비스/정책)

…/gesellschaft-application
  ├─ service: usecases 구현(읽기 중심 + 어드민 커맨드)
  ├─ dto: Read/Write DTO, Query 파라미터
  └─ mapper: domain↔dto 변환

…/gesellschaft-infrastructure
  ├─ jpa: *Jpa 엔티티, Spring Data Repository
  ├─ adapter: PersonaRepositoryAdapter, MigrationAdapter(Jinx)
  └─ config: JPA, Tx, MySQL, Cache

…/gesellschaft-boot
  ├─ api: public 컨트롤러(Query)
  ├─ admin: 관리 컨트롤러(CRUD)
  └─ docs: Swagger/OpenAPI
```

---

## 7) 마이그레이션(Jinx) 연동

* 전략: **엔티티 변경 → Jinx로 DDL 생성 → 운영 반영**
* 권장 흐름

    1. `gesellschaft-infrastructure`의 JPA 엔티티 수정
    2. `jinx-gradle-plugin` or CLI로 **diff** 생성
    3. `liquibase` 폴더에 YAML/SQL 출력(옵션)
    4. CI에서 스키마 검증/시뮬레이션

> Jinx 옵션 설계 시, **phase 기반 점진 제거**·**컬럼 리네임**·**데이터 백필**은 차기 단계에서 도입 예정(DDL+DML 통합 전략).

---

## 8) 성능/검색 전략(초안)

* 기본: RDB 정규화 + 필요한 곳만 인덱스(코드, 외래키, `sin_affinity`, `attack_type`)
* 조회 API는 **읽기 전용**이므로 **캐시(Redis) 고려**
* 고급 검색(태그/효과 텍스트 포함)은 추후 **전문 검색(ES)** 어댑터로 분리

---

## 9) 품질/테스트

* 계층별 테스트

    * Domain: 순수 단위 테스트
    * Application: 유스케이스 단위/시나리오 테스트
    * Infrastructure: JPA 슬라이스, Testcontainers(MySQL)
    * Boot: REST 컨트롤러 통합 테스트
* CI: 빌드/테스트, Jinx diff 검증, OpenAPI 스냅샷

---

## 10) 로드맵

* [ ] **메타데이터 확정**: Skill 세부 필드(코인 스펙, 태그, 효과 텍스트 규칙)
* [ ] **어드민 UI 스펙**: 입력/검증 UX, 배치 Import(스프레드시트→CSV)
* [ ] **Public API v1**: 조회 엔드포인트 확정 & 문서화
* [ ] **추천 시스템 v0**: 간단한 규칙 기반 조합 추천(속성/타입/태그 매칭)
* [ ] **DSL 초안**: Persona/Skill을 기술하는 **게임 전용 DSL**(스키마/파서/검증기)
* [ ] **성능/캐시링**: 인기 조회 캐싱, 슬로우쿼리 튜닝

---

## 11) 합의가 필요한 포인트(체크리스트)

* [ ] 동기화 레벨별 스킬 차이의 **데이터 표현 방식**(상속 vs 버전드 엔티티 vs 별도 테이블)
* [ ] **코인 규칙**의 필드 구성(개수/가중치/가변 파라미터)
* [ ] **스킬 태그/효과 텍스트** 표준화(다국어 로케일 정책)
* [ ] **인격 희귀도/출시 시즌** 등 카탈로그 속성 정의
* [ ] Admin 보호 방식(토큰/헤더 키, 향후 계정 도입 시 전환 경로)

---

## 12) 예시 시나리오

1. 사용자가 `슬래시 + PRIDE` 속성의 스킬을 가진 인격을 찾는다 → `/api/skills?type=SLASH&sin=PRIDE` 조회
2. 어드민이 새 인격을 등록한다 → Persona 생성 → Sync 1~4 생성 → 각 Sync에 스킬 등록
3. 추천 v0: 선택한 전투 컨셉(예: 출혈 기반) → 해당 태그/효과를 가진 스킬 조합 추천

