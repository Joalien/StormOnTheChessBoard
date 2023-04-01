package board;

import card.SCCard;
import piece.Piece;

public class ChessBoardFacade {

    private ChessBoard chessBoard;
    public ChessBoardFacade() {
    }

    public void startGame() {
        this.chessBoard = ChessBoard.createWithInitialState();
    }

    public boolean tryToMove(String from, String to) {
        return chessBoard.tryToMove(from, to);
    }

    public boolean tryToPlayCard(SCCard card) {
        return card.playOn(chessBoard);
    }
}
