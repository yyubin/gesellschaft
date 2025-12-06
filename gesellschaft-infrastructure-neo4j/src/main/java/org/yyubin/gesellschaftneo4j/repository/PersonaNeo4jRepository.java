package org.yyubin.gesellschaftneo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.yyubin.gesellschaftneo4j.node.PersonaNode;

import java.util.List;

public interface PersonaNeo4jRepository extends Neo4jRepository<PersonaNode, Long> {

    // 등급(grade)으로 인격 조회
    @Query("MATCH (p:Persona) WHERE p.grade = $grade RETURN p")
    List<PersonaNode> findByGrade(@Param("grade") String grade);

    // 죄악(Sin) 속성으로 인격 조회
    @Query("MATCH (p:Persona)-[:HAS_SKILL]->(s:Skill)-[:USES_SIN]->(sin:SinAffinity) " +
            "WHERE sin.type = $sinType " +
            "RETURN DISTINCT p")
    List<PersonaNode> findBySinAffinity(@Param("sinType") String sinType);

    // 스테이터스 효과 키워드로 인격 조회
    @Query("MATCH (p:Persona)-[:HAS_SKILL]->(s:Skill)-[:HAS_KEYWORD]->(status:StatusEffect) " +
            "WHERE status.type = $statusType " +
            "RETURN DISTINCT p")
    List<PersonaNode> findByStatusEffectKeyword(@Param("statusType") String statusType);

    // 스킬 키워드 기반의 유사 인격 추천 조회
    @Query("MATCH (p:Persona {id: $personaId})-[:HAS_SKILL]->(s1:Skill)-[:HAS_KEYWORD]->(status:StatusEffect)<-[:HAS_KEYWORD]-(s2:Skill)<-[:HAS_SKILL]-(similar:Persona) " +
            "WHERE p.id <> similar.id " +
            "RETURN DISTINCT similar " +
            "LIMIT $limit")
    List<PersonaNode> findSimilarPersonas(@Param("personaId") Long personaId, @Param("limit") int limit);

    // 인격 상세 조회 (스킬/패시브 포함)
    @Query("MATCH (p:Persona) WHERE p.id = $id " +
            "OPTIONAL MATCH (p)-[:HAS_SKILL]->(skill:Skill) " +
            "OPTIONAL MATCH (p)-[:HAS_PASSIVE]->(passive:Passive) " +
            "RETURN p, collect(DISTINCT skill), collect(DISTINCT passive)")
    PersonaNode findByIdWithDetails(@Param("id") Long id);
}
