package fr.kubys.piece;

import fr.kubys.core.Color;

public enum PromotionPiece {
    QUEEN, ROOK, BISHOP, KNIGHT;

    public Piece toPiece(Color color) {
        return switch (this) {
            case QUEEN  -> new Queen(color);
            case ROOK   -> new Rock(color);
            case BISHOP -> new Bishop(color);
            case KNIGHT -> new Knight(color);
        };
    }
}
