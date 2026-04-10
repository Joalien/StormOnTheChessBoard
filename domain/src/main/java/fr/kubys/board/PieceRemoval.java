package fr.kubys.board;

import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

public record PieceRemoval(Piece piece, Position position, int turn, RemovalReason reason) {

    public enum RemovalReason {
        CAPTURED,
        EFFECT
    }
}
