package model.skill;

import java.util.Map;
import lombok.Getter;

@Getter
public class SkillTrigger {
    private final TriggerCode code;
    private final Map<String, Object> subFlags;

    public SkillTrigger(TriggerCode code, Map<String, Object> subFlags) {
        this.code = code;
        this.subFlags = subFlags;
    }
}
