package org.yyubin.gesellschaftinfrastructure.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.Converter;
import model.skill.effect.EffectAction.AmountExpression;

/**
 * AmountExpression (Record)를 JSON으로 변환하는 컨버터
 * - AmountExpression은 EffectAction 내부의 public static record
 */
@Converter(autoApply = false)
public class AmountExpressionConverter extends JsonConverter<AmountExpression> {

    public AmountExpressionConverter() {
        super(new TypeReference<AmountExpression>() {});
    }
}
