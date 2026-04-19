package fr.kubys.card.params;

import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

public record DoubleStrikeCardParam(Piece piece1, Position position1, Piece piece2, Position position2) implements CardParam {
}
