package org.yyubin.gesellschaftboot.dto;

import dto.SubAffiliationResponse;
import model.MainAffiliationCategory;

public record SubAffiliationResponseDto(
    Long id,
    String name,
    MainAffiliationCategory mainCategory
) {
    public static SubAffiliationResponseDto from(SubAffiliationResponse response) {
        if (response == null) return null;
        return new SubAffiliationResponseDto(
            response.id(),
            response.name(),
            response.mainCategory()
        );
    }
}
