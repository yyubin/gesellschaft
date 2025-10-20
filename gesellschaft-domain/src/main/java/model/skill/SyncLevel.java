package model.skill;

/**
 * 동기화 레벨
 */
public enum SyncLevel {
    SYNC_1(1),
    SYNC_2(2),
    SYNC_3(3),
    SYNC_4(4);

    private final int level;

    SyncLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static SyncLevel fromInt(int level) {
        return switch (level) {
            case 1 -> SYNC_1;
            case 2 -> SYNC_2;
            case 3 -> SYNC_3;
            case 4 -> SYNC_4;
            default -> throw new IllegalArgumentException("Invalid sync level: " + level);
        };
    }
}
