package fr.kubys.board.effect;

import fr.kubys.piece.Piece;

public class NeutralityEffect extends Effect {

    private final Piece piece;

    public NeutralityEffect(Piece piece) {
        super("Neutralité");
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }
}
