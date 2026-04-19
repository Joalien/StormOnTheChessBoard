package fr.kubys.card.params;

import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.List;

public record LeapfrogCardParam(Piece pawn, List<Position> positions) implements CardParam {
}
