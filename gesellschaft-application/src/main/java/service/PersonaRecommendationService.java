package service;

import dto.PersonaResponse;
import java.util.List;
import query.GetSimilarPersonasQuery;

public interface PersonaRecommendationService {
    List<PersonaResponse> getSimilarPersonas(GetSimilarPersonasQuery query);
}
