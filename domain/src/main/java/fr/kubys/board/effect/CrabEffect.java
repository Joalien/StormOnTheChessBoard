package fr.kubys.board.effect;

import fr.kubys.piece.Piece;

public class CrabEffect extends Effect {

    private final Piece piece;

    public CrabEffect(Piece piece) {
        super("Crabe");
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }
}
