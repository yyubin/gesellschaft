package port;

import model.persona.Persona;

import java.util.List;

/**
 * 인격 추천 Repository (Neo4j 기반)
 * - 스킬 키워드 기반 유사 인격 추천
 */
public interface PersonaRecommendationRepository {

    /**
     * 스킬 키워드 기반 유사 인격 조회
     * @param personaId 기준 인격 ID
     * @param limit 조회 개수
     * @return 유사 인격 목록
     */
    List<Persona> findSimilarPersonas(Long personaId, int limit);
}
