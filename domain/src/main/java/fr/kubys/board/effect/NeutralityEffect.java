package fr.kubys.board.effect;

import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.Collections;
import java.util.List;

public class NeutralityEffect extends Effect {

    private final Piece piece;

    public NeutralityEffect(Piece piece) {
        super("Neutralité");
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }

    @Override
    public List<Position> getPositions() {
        return piece.getPosition() != null ? List.of(piece.getPosition()) : Collections.emptyList();
    }
}
