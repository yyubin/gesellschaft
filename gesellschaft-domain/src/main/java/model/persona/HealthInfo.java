package model.persona;

import java.util.Objects;
import lombok.Getter;

@Getter
public class HealthInfo {
    private final int baseHealth;
    private final double growthRate;
    private final int disturbed1;
    private final int disturbed2;
    private final int disturbed3;

    public HealthInfo(int baseHealth, double growthRate, int disturbed1, int disturbed2, int disturbed3) {
        this.baseHealth = Objects.requireNonNull(baseHealth);
        this.growthRate = Objects.requireNonNull(growthRate);
        this.disturbed1 = disturbed1;
        this.disturbed2 = disturbed2;
        this.disturbed3 = disturbed3;
    }

    public int calculateHealthAtLevel(int level) {
        return (int) (baseHealth + growthRate * (level - 1));
    }
}
