# **Gesellschaft Backend**

게임 *림버스 컴퍼니*의 인격(Persona)·스킬 데이터를 기반으로
**RDB + Neo4j 조합 아키텍처**, **GraphQL API**, **추천 시스템**을 갖춘 백엔드 서비스입니다.

---

## 🚀 **기술 스택**

* **Kotlin/Spring Boot**
* **GraphQL (Spring for GraphQL)**
* **MySQL (RDB)** – 기본 정보 저장
* **Neo4j** – 스킬 관계 기반 추천 시스템
* **Hexagonal Architecture / Multi-module**
* **Docker Compose 지원 예정**

---

## 🧱 **아키텍처 개요**

```
boot-api  — GraphQL Resolver / DTO / Config
application — Service, Facade, Query/Command
domain — Persona, Skill, Value Objects
infrastructure-rdb — JPA Repository, RDB Adapter
infrastructure-neo4j — Neo4j Repository, Adapter, Mapper
```

* **RDB**: 인격, 스킬, 시즌, 속성 등 정규화된 게임 정보 저장
* **Neo4j**: Persona–Skill–StatusEffect 관계 그래프 기반 추천 기능 구현
* **GraphQL**: 커서 기반 페이지네이션, Persona 조회/추천 API 제공
* **Facade 패턴**: Resolver 비대화를 막고 서비스 조합 책임을 분리

---

## **핵심 기능**

### 1) **Persona 조회 API**

* 등급/수감자/시즌/속성 기반 조회
* 커서 페이지네이션 지원 (forward/backward 모두 가능)

### 2) **추천 시스템 (Neo4j 기반)**

* 스킬 키워드, 상태효과, 시너지 관계를 기반으로
  **유사 인격 추천(Persona Recommendation)** 제공

### 3) **Persona 상세 조회**

* 스킬, 패시브, 효과, 코인, 트리거까지 포함한 전체 트리 구조 반환
