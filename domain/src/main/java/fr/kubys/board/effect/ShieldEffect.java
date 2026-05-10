package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.List;

public class ShieldEffect extends Effect {
    private final Piece protectedPiece;

    public ShieldEffect(Piece protectedPiece) {
        super("Bouclier");
        this.protectedPiece = protectedPiece;
    }

    @Override
    public List<Position> getPositions() {
        return protectedPiece.findPosition()
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public boolean blocksPosition(Position position) {
        return protectedPiece.findPosition()
                .map(position::equals)
                .orElse(false);
    }

    /**
     * The shield protects during the opponent's single upcoming turn (the card is played
     * AFTER_TURN, so the next move belongs to the opponent). Once the opponent has actually
     * moved — whether they captured something else, advanced a pawn, or did anything at all
     * — the shield's job is over and the effect removes itself. We discriminate a real move
     * from an incidental {@code add()} (which also fires this hook) by comparing against
     * {@code lastMovedPiece}, the same convention used by {@link AstralTravelEffect}.
     */
    @Override
    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
        if (piece == chessBoard.getLastMovedPiece() && piece.getColor() != protectedPiece.getColor()) {
            chessBoard.removeEffect(this);
        }
    }

    @Override
    public void afterRemovingPieceHook(ChessBoard chessBoard, Piece piece) {
        if (piece == protectedPiece) {
            chessBoard.removeEffect(this);
        }
    }
}
