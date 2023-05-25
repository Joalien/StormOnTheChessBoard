package board.effect;

import board.ChessBoard;
import lombok.extern.slf4j.Slf4j;
import piece.Piece;

@Slf4j
public class MagnetismEffect extends Effect {
    private final Piece piece;

    public MagnetismEffect(Piece piece) {
        super("Magnétisme");
        this.piece = piece;
    }

    @Override
    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
        if (this.piece.equals(piece)) chessBoard.removeEffect(this);
    }

    @Override
    public void beforeMoveHook(ChessBoard chessBoard, Piece piece) {
        if (!this.piece.equals(piece) && this.piece.getPosition().hasNoPositionBetween(piece.getPosition()))
            throw new MagnetismException("%s cannot move because it is near %s".formatted(piece, this.piece));
    }

    @Override
    public void afterRemovingPieceHook(ChessBoard chessBoard, Piece piece) {
        if (this.piece.equals(piece)) chessBoard.removeEffect(this);
    }
}