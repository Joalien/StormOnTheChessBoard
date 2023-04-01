package board;

import card.SCCard;
import piece.Piece;

public class ChessBoardFacade {

    private final ChessBoard chessBoard;
    public ChessBoardFacade(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    public boolean tryToMove(String from, String to) {
        return chessBoard.tryToMove(from, to);
    }

    public boolean tryToMove(Piece piece, String positionToMoveOn) {
        return chessBoard.tryToMove(piece, positionToMoveOn);
    }

    public boolean tryToPlayCard(SCCard card) {
        return card.playOn(chessBoard);
    }
}
