package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.List;

public class HideoutEffect extends Effect {

    private final Piece frozenPiece;

    public HideoutEffect(Piece frozenPiece) {
        super("Planque");
        this.frozenPiece = frozenPiece;
    }

    public Piece getFrozenPiece() {
        return frozenPiece;
    }

    @Override
    public List<Position> getPositions() {
        return frozenPiece.findPosition()
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public boolean blocksAttack(Piece attacker, Position target) {
        // Sleeping piece cannot attack or give check
        return attacker == frozenPiece;
    }

    @Override
    public void beforeMoveHook(ChessBoard chessBoard, Piece piece) {
        if (piece == frozenPiece) {
            throw new IllegalStateException("Cette pièce est endormie et ne peut pas bouger");
        }
    }

    @Override
    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
        // After any move, check if the frozen piece's king is in check.
        // If so, the piece wakes up (effect is removed).
        if (chessBoard.isKingUnderAttack(frozenPiece.getColor())) {
            chessBoard.removeEffect(this);
        }
    }

    @Override
    public void afterRemovingPieceHook(ChessBoard chessBoard, Piece piece) {
        // If the frozen piece is captured, remove the effect
        if (piece == frozenPiece) {
            chessBoard.removeEffect(this);
        }
    }
}
