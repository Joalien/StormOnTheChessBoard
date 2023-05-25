package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;
import lombok.Getter;

@Getter
public abstract class Effect {

    private final String name;

    public Effect(String name) {
        this.name = name;
    }

    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
    }

    public void beforeMoveHook(ChessBoard chessBoard, Piece piece) {
    }

    public void afterRemovingPieceHook(ChessBoard chessBoard, Piece piece) {
    }

    public boolean allowToMove(Piece piece, Position positionToMoveOn) {
        return false;
    }
}
