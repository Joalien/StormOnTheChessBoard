package effet;

import board.ChessBoard;
import lombok.extern.slf4j.Slf4j;
import piece.Color;
import piece.King;
import piece.Piece;

@Slf4j
public class BombingEffect extends Effect {
    private final String position;
    private final Color isPlayedBy;

    public BombingEffect(String position, Color isPlayedBy) {
        super("Attentat");
        this.position = position;
        this.isPlayedBy = isPlayedBy;
    }

    @Override
    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
        if (piece.getPosition().equals(this.position) && piece.getColor() != isPlayedBy) {
            log.info("BOUM!");
            if (!(piece instanceof King)) {
                chessBoard.removePieceFromTheBoard(piece);
            }
            chessBoard.removeEffect(this);
        }
    }
}
