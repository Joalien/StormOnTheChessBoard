package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.Piece;

public class MaddeningSquareEffect extends Effect {

    private final ChessBoard chessBoard;
    private final Position maddeningSquare;

    public MaddeningSquareEffect(ChessBoard chessBoard, Position maddeningSquare) {
        super("La Case qui Rend Fou");
        this.chessBoard = chessBoard;
        this.maddeningSquare = maddeningSquare;
        this.getPositions().add(maddeningSquare);
    }

    public Position getMaddeningSquare() {
        return maddeningSquare;
    }

    @Override
    public boolean allowToMove(Piece piece, Position positionToMoveOn) {
        if (!maddeningSquare.equals(piece.getPosition())) return false;

        Bishop tempBishop = new Bishop(piece.getColor());
        tempBishop.setPosition(piece.getPosition());
        return tempBishop.isPositionTheoreticallyReachable(positionToMoveOn)
                && chessBoard.emptyPath(tempBishop, positionToMoveOn);
    }
}
