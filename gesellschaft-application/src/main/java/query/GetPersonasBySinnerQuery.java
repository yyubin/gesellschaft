package query;

public record GetPersonasBySinnerQuery(Long sinnerId) {

    public GetPersonasBySinnerQuery {
        if (sinnerId == null || sinnerId <= 0) {
            throw new IllegalArgumentException("Sinner ID must be positive");
        }
    }
}
