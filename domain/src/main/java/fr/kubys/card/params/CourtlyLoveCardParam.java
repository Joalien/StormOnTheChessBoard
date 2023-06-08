package fr.kubys.card.params;

import fr.kubys.core.Position;
import fr.kubys.piece.Knight;

public record CourtlyLoveCardParam(Knight knight, Position positionToMoveOn) implements CardParam {
}
