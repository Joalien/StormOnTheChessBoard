package effet;

import board.ChessBoard;
import lombok.extern.slf4j.Slf4j;
import piece.Color;
import piece.King;
import piece.Piece;

@Slf4j
public class BombingEffect extends Effect {
    private final String position;
    private final Color colorThatShouldExplode;

    public BombingEffect(String position, Color colorThatShouldExplode) {
        this.position = position;
        this.colorThatShouldExplode = colorThatShouldExplode;
    }

    @Override
    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
        if (piece.getPosition().equals(this.position) && piece.getColor() == colorThatShouldExplode) {
            log.info("BOUM!");
            if (!(piece instanceof King)) {
                chessBoard.removePieceFromTheBoard(piece);
            }
            chessBoard.removeEffect(this);
        }
    }
}
