package org.yyubin.gesellschaftinfrastructure.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Converter;
import model.skill.effect.EffectAction.AmountExpression;

@Converter(autoApply = false)
public class AmountExpressionConverter extends JsonConverter<AmountExpression> {

    public AmountExpressionConverter() {
        super(new TypeReference<AmountExpression>() {});
    }
}
