package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.King;
import fr.kubys.piece.Piece;
//import org.slf4j.Logger;

public class BombingEffect extends Effect {
    private final Position position;
    private final Color isPlayedBy;
    //    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BombingEffect.class);


    public BombingEffect(Position position, Color isPlayedBy) {
        super("Attentat");
        this.position = position;
        this.isPlayedBy = isPlayedBy;
    }

    @Override
    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
        if (piece.getPosition().equals(this.position) && piece.getColor() != isPlayedBy) {
//            log.info("BOUM!");
            if (!(piece instanceof King)) {
                chessBoard.removePieceFromTheBoard(piece);
            }
            chessBoard.removeEffect(this);
        }
    }
}
