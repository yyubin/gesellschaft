package model.persona;

import java.util.Objects;
import lombok.Getter;

@Getter
public class SpeedInfo {
    private final int minSpeed;
    private final int maxSpeed;

    public SpeedInfo(int minSpeed, int maxSpeed) {
        this.minSpeed = Objects.requireNonNull(minSpeed);
        this.maxSpeed = Objects.requireNonNull(maxSpeed);
    }

    public int randomSpeed() {
        return (int) (Math.random() * (maxSpeed - minSpeed + 1)) + minSpeed;
    }
}
