package fr.kubys.card.params;

import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.List;

public record SerialKillerCardParam(Piece pawn, List<Position> positions) implements CardParam {
}
