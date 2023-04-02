package effet;

import board.ChessBoard;
import lombok.Getter;
import piece.Piece;

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

    public boolean allowToMove(Piece piece, String positionToMoveOn) {
        return false;
    }
}
