package fr.kubys.card.params;

import fr.kubys.piece.Piece;

import java.util.Set;

public record InfiltrationCardParam(Set<Piece> pawns) implements CardParam {
}
