package org.yyubin.gesellschaftinfrastructure.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Converter;
import model.skill.SkillTrigger;

@Converter(autoApply = false)
public class SkillTriggerConverter extends JsonConverter<SkillTrigger> {

    public SkillTriggerConverter() {
        super(new TypeReference<SkillTrigger>() {});
    }
}
