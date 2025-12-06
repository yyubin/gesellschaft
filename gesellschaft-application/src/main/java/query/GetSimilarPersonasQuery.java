package query;

public record GetSimilarPersonasQuery(Long personaId, Integer limit) {
    public GetSimilarPersonasQuery {
        if (personaId == null) {
            throw new IllegalArgumentException("personaId cannot be null");
        }
        if (limit == null || limit <= 0) {
            limit = 5; // 기본값
        }
    }

    public int getLimit() {
        return limit != null ? limit : 5;
    }
}
