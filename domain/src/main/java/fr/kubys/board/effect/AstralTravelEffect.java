package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.List;
import java.util.Set;

public class AstralTravelEffect extends Effect {
    private final Piece removedPiece;
    private final Position originalPosition;
    private boolean restored = false;

    public AstralTravelEffect(Piece removedPiece, Position originalPosition) {
        super("AstralTravelEffect");
        this.removedPiece = removedPiece;
        this.originalPosition = originalPosition;
    }

    @Override
    public List<Position> getPositions() {
        return List.of(originalPosition);
    }

    @Override
    public boolean blocksPosition(Position position) {
        return !restored && position.equals(originalPosition);
    }

    @Override
    public boolean blocksPath(Position from, Position to, Set<Position> intermediates) {
        return false;
    }

    @Override
    public void afterMoveHook(ChessBoard board, Piece piece) {
        // After a move is made (the player's move), restore the piece
        if (!restored && piece != removedPiece) {
            restored = true;
            board.removeEffect(this);
            if (board.at(originalPosition).getPiece().isEmpty()) {
                board.add(removedPiece, originalPosition);
            }
        }
    }
}
