package card;

import board.ChessBoard;
import piece.Knight;
import piece.extra.Kangaroo;

public class KangarooCard extends Card {

    private Knight knight;

    public KangarooCard(Knight knight) {
        super("Kangaroo", "Transformez définitivement l'un de vos cavaliers, ou un cavalier adverse en kangourou. Le kangourou se déplace en faisant deux sauts de cavalier consécutifs.", SCType.AFTER_TURN);
        this.knight = knight;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (knight == null) throw new IllegalStateException();
        if (chessBoard.getOutOfTheBoardPieces().contains(knight))
            throw new IllegalArgumentException("%s should be on the board".formatted(knight));
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true; // FIXME
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        String knightPosition = knight.getPosition();
        chessBoard.removePieceFromTheBoard(knight);
        chessBoard.add(new Kangaroo(knight.getColor()), knightPosition);
        return true;
    }
}
