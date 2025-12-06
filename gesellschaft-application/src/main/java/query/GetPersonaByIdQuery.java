package query;

public record GetPersonaByIdQuery(Long id) {

    public GetPersonaByIdQuery {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Persona ID must be positive");
        }
    }
}
