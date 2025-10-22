package org.yyubin.gesellschaftinfrastructure.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Converter;
import model.skill.effect.TargetSelector;

/**
 * TargetSelector를 JSON으로 변환하는 컨버터
 */
@Converter(autoApply = false)
public class TargetSelectorConverter extends JsonConverter<TargetSelector> {

    public TargetSelectorConverter() {
        super(new TypeReference<TargetSelector>() {});
    }
}
