package dto;

public record PersonaEdgeResponse(
    PersonaResponse node,
    String cursor
) {
    public static PersonaEdgeResponse of(PersonaResponse node, String cursor) {
        return new PersonaEdgeResponse(node, cursor);
    }
}
