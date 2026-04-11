package fr.kubys.board.effect;

import fr.kubys.piece.Piece;

public class PieceTransformationEffect extends Effect {

    private final Piece piece;

    public PieceTransformationEffect(String name, Piece piece) {
        super(name);
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }
}
