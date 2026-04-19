package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.List;

public class DunceCapEffect extends Effect {

    private final Piece frozenPiece;
    private final int createdOnTurn;

    public DunceCapEffect(Piece frozenPiece, int createdOnTurn) {
        super("Bonnet d'Âne");
        this.frozenPiece = frozenPiece;
        this.createdOnTurn = createdOnTurn;
    }

    @Override
    public List<Position> getPositions() {
        return frozenPiece.findPosition()
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public void beforeMoveHook(ChessBoard chessBoard, Piece piece) {
        if (piece == frozenPiece) {
            if (chessBoard.getTurnNumber() <= createdOnTurn + 1) {
                throw new IllegalStateException("Cette pièce est au piquet et ne peut pas bouger");
            } else {
                chessBoard.removeEffect(this);
            }
        }
    }

    @Override
    public void afterRemovingPieceHook(ChessBoard chessBoard, Piece piece) {
        if (piece == frozenPiece) {
            chessBoard.removeEffect(this);
        }
    }
}
