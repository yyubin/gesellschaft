package org.yyubin.gesellschaftinfrastructure.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Converter;
import model.skill.effect.CoinSelector;

@Converter(autoApply = false)
public class CoinSelectorConverter extends JsonConverter<CoinSelector> {

    public CoinSelectorConverter() {
        super(new TypeReference<CoinSelector>() {});
    }
}
