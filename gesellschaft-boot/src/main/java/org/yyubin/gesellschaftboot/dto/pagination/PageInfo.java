package org.yyubin.gesellschaftboot.dto.pagination;

public record PageInfo(
    boolean hasNextPage,
    boolean hasPreviousPage,
    String startCursor,
    String endCursor
) {
    public static PageInfo of(boolean hasNextPage, boolean hasPreviousPage, String startCursor, String endCursor) {
        return new PageInfo(hasNextPage, hasPreviousPage, startCursor, endCursor);
    }
}
