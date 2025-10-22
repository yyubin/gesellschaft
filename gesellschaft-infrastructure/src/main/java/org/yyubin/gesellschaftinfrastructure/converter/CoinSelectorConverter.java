package org.yyubin.gesellschaftinfrastructure.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Converter;
import model.skill.effect.CoinSelector;

/**
 * CoinSelector를 JSON으로 변환하는 컨버터
 */
@Converter(autoApply = false)
public class CoinSelectorConverter extends JsonConverter<CoinSelector> {

    public CoinSelectorConverter() {
        super(new TypeReference<CoinSelector>() {});
    }
}
