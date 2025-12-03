package dto;

import model.MainAffiliationCategory;
import model.SubAffiliation;

public record SubAffiliationResponse(
    Long id,
    String name,
    MainAffiliationCategory mainCategory
) {
    public static SubAffiliationResponse from(SubAffiliation subAffiliation) {
        if (subAffiliation == null) {
            return null;
        }
        return new SubAffiliationResponse(
            subAffiliation.getId(),
            subAffiliation.getName(),
            subAffiliation.getMainCategory()
        );
    }
}
