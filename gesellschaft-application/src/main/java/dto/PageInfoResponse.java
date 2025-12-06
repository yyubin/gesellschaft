package dto;

public record PageInfoResponse(
    boolean hasNextPage,
    boolean hasPreviousPage,
    String startCursor,
    String endCursor
) {
    public static PageInfoResponse of(boolean hasNextPage, boolean hasPreviousPage,
                                      String startCursor, String endCursor) {
        return new PageInfoResponse(hasNextPage, hasPreviousPage, startCursor, endCursor);
    }
}
