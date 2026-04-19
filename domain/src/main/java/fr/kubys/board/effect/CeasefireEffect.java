package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.Collections;
import java.util.List;

public class CeasefireEffect extends Effect {

    private final ChessBoard chessBoard;

    public CeasefireEffect(ChessBoard chessBoard) {
        super("Cessez le Feu");
        this.chessBoard = chessBoard;
    }

    @Override
    public List<Position> getPositions() {
        return Collections.emptyList();
    }

    @Override
    public boolean blocksPosition(Position position) {
        // Block positions occupied by non-king pieces.
        // Kings are excluded so that check detection still works
        // (canAttack needs to "reach" the king's position).
        return chessBoard.at(position).getPiece()
                .filter(p -> !p.isKing())
                .isPresent();
    }

    @Override
    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
        // After a move, check if either king is in check. If so, remove the effect.
        if (chessBoard.isKingUnderAttack(Color.WHITE) || chessBoard.isKingUnderAttack(Color.BLACK)) {
            chessBoard.removeEffect(this);
        }
    }
}
