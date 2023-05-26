package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Knight;
import fr.kubys.piece.extra.Kangaroo;

import java.util.List;

public class KangarooCard extends Card {

    private Knight knight;

    public KangarooCard() {
        super("Kangaroo", "Transformez définitivement l'un de vos cavaliers, ou un cavalier adverse en kangourou. Le kangourou se déplace en faisant deux sauts de cavalier consécutifs.", CardType.AFTER_TURN);
    }

    @Override
    protected void setupParams(List<?> params) {
        this.knight = (Knight) params.get(0);
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
    protected void doAction(ChessBoard chessBoard) {
        Position knightPosition = knight.getPosition();
        chessBoard.removePieceFromTheBoard(knight);
        chessBoard.add(new Kangaroo(knight.getColor()), knightPosition);
    }
}
