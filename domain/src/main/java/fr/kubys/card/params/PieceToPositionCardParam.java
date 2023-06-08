package fr.kubys.card.params;

import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

public record PieceToPositionCardParam(Piece piece, Position positionToMoveOn) implements CardParam {
}
