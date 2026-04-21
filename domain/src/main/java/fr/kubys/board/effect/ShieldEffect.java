package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.List;

public class ShieldEffect extends Effect {
    private final Piece protectedPiece;
    private final int createdOnTurn;

    public ShieldEffect(Piece protectedPiece, int createdOnTurn) {
        super("Bouclier");
        this.protectedPiece = protectedPiece;
        this.createdOnTurn = createdOnTurn;
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

    @Override
    public void beforeMoveHook(ChessBoard chessBoard, Piece piece) {
        if (piece.getColor() == protectedPiece.getColor() && chessBoard.getTurnNumber() > createdOnTurn) {
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
