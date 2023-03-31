package board;

import piece.Color;
import piece.Piece;

public class BombingEffect extends Effect {
    private final String position;
    private final Color color;

    public BombingEffect(String position, Color color) {
        this.position = position;
        this.color = color;
    }

    @Override
    public void afterMoveHook(ChessBoard chessBoard, Piece piece, String position) {
        if (piece.getPosition().equals(position)) {
            System.out.println("BOUM!");
            chessBoard.removePieceFromTheBoard(piece);
        }
    }
}
