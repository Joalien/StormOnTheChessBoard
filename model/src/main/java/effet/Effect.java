package effet;

import board.ChessBoard;
import piece.Piece;

public abstract class Effect {

    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {}

    public void beforeMoveHook(ChessBoard chessBoard, Piece piece) {}

    public void afterRemovingPieceHook(ChessBoard chessBoard, Piece piece) {}
}
