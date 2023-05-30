package fr.kubys.card.params;

import fr.kubys.card.params.CardParam;
import fr.kubys.piece.Piece;

public record PieceCardParam(Piece piece) implements CardParam {
}
