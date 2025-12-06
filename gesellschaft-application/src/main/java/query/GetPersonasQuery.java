package query;

/**
 * Query for retrieving personas with cursor-based pagination
 * Relay Cursor Connections 표준을 따름
 */
public record GetPersonasQuery(
    Integer first,
    String after,
    Integer last,
    String before
) {
    private static final int DEFAULT_PAGE_SIZE = 20;

    public GetPersonasQuery {
        if (first != null && last != null) {
            throw new IllegalArgumentException("Cannot use both 'first' and 'last' arguments");
        }
        if (after != null && before != null) {
            throw new IllegalArgumentException("Cannot use both 'after' and 'before' cursors");
        }
        if (first != null && first <= 0) {
            throw new IllegalArgumentException("'first' must be positive");
        }
        if (last != null && last <= 0) {
            throw new IllegalArgumentException("'last' must be positive");
        }
    }

    public int getPageSize() {
        int size = first != null ? first : (last != null ? last : DEFAULT_PAGE_SIZE);
        return size + 1;
    }

    public int getActualLimit() {
        return first != null ? first : (last != null ? last : DEFAULT_PAGE_SIZE);
    }

    public boolean isForward() {
        return first != null || after != null;
    }

    public boolean isBackward() {
        return last != null || before != null;
    }
}
