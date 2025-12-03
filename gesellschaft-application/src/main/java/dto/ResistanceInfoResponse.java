package dto;

import model.persona.ResistanceInfo;
import model.persona.ResistanceType;

public record ResistanceInfoResponse(
    ResistanceType slashResistance,
    ResistanceType penetrationResistance,
    ResistanceType bluntResistance
) {
    public static ResistanceInfoResponse from(ResistanceInfo resistanceInfo) {
        if (resistanceInfo == null) {
            return null;
        }
        return new ResistanceInfoResponse(
            resistanceInfo.getSlashResistance(),
            resistanceInfo.getPenetrationResistance(),
            resistanceInfo.getBluntResistance()
        );
    }
}
