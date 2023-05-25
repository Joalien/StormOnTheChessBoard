package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.King;
import fr.kubys.piece.Piece;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BombingEffect extends Effect {
    private final Position position;
    private final Color isPlayedBy;

    public BombingEffect(Position position, Color isPlayedBy) {
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
