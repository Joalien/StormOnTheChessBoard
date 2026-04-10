package fr.kubys.card.params;

import fr.kubys.piece.Piece;

public record TwoPieceCardParam(Piece piece1, Piece piece2) implements CardParam {
}
