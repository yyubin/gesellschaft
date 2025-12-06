package org.yyubin.gesellschaftboot.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CursorUtil {

    private static final String CURSOR_PREFIX = "cursor:";

    public static String encode(Long id) {
        if (id == null) {
            return null;
        }
        String value = CURSOR_PREFIX + id;
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static Long decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            if (!decoded.startsWith(CURSOR_PREFIX)) {
                throw new IllegalArgumentException("Invalid cursor format");
            }
            return Long.parseLong(decoded.substring(CURSOR_PREFIX.length()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor: " + cursor, e);
        }
    }
}
