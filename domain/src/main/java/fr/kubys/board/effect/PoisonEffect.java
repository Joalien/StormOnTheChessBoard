package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.List;
import java.util.Optional;

public class PoisonEffect extends Effect {
    private final Piece poisonedPiece;
    private Optional<Position> poisonedPosition = Optional.empty();

    public PoisonEffect(Piece poisonedPiece) {
        super("Poison");
        this.poisonedPiece = poisonedPiece;
    }

    @Override
    public List<Position> getPositions() {
        return poisonedPiece.findPosition()
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public void beforeMoveHook(ChessBoard chessBoard, Piece piece) {
        poisonedPosition = poisonedPiece.findPosition();
    }

    @Override
    public void afterMoveHook(ChessBoard board, Piece piece) {
        if (piece == poisonedPiece) return;
        poisonedPosition
                .filter(pos -> poisonedPiece.getPosition() == null)
                .filter(pos -> piece.getPosition().equals(pos))
                .filter(pos -> piece.getColor() != poisonedPiece.getColor())
                .ifPresent(pos -> {
                    board.removePieceFromTheBoard(piece);
                    board.removeEffect(this);
                });
    }
}
