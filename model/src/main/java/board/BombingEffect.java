package board;

import lombok.extern.slf4j.Slf4j;
import piece.Color;
import piece.King;
import piece.Piece;

@Slf4j
public class BombingEffect extends Effect {
    private final String position;
    private final Color color;

    public BombingEffect(String position, Color color) {
        this.position = position;
        this.color = color;
    }

    @Override
    public void afterMoveHook(ChessBoard chessBoard, Piece piece, String position) {
        if (piece.getPosition().equals(this.position) && piece.getColor() != color) {
            log.info("BOUM!");
            if (!(piece instanceof King)) {
                chessBoard.removePieceFromTheBoard(piece);
            }
            chessBoard.removeEffect(this);
        }
    }
}
