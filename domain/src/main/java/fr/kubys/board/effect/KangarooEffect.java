package fr.kubys.board.effect;

import fr.kubys.piece.Piece;

public class KangarooEffect extends Effect {

    private final Piece piece;

    public KangarooEffect(Piece piece) {
        super("Kangourou");
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }
}
