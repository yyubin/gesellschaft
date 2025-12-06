package dto;

import java.util.List;

public record PersonaConnectionResponse(
    List<PersonaEdgeResponse> edges,
    PageInfoResponse pageInfo,
    Integer totalCount
) {
    public static PersonaConnectionResponse of(List<PersonaEdgeResponse> edges,
                                                PageInfoResponse pageInfo,
                                                Integer totalCount) {
        return new PersonaConnectionResponse(edges, pageInfo, totalCount);
    }
}
