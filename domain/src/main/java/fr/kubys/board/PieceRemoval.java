package fr.kubys.board;

import fr.kubys.piece.Piece;

public record PieceRemoval(Piece piece, int turn, RemovalReason reason) {

    public enum RemovalReason {
        CAPTURED,
        EFFECT
    }
}
