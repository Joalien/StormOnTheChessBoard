package card;

import board.ChessBoard;
import piece.extra.BlackHole;

import java.util.List;

public class BlackHoleCard extends Card {

    private String position;

    public BlackHoleCard() {
        super("Trou noir", "Désignez une case vide qui est \"retirée\" de l'échiquier juqu'à la fin de la partie. Il sera impossible pendant la suite du jeu de s'y arrêter ou de la traverser.", CardType.AFTER_TURN);
    }

    @Override
    protected void setupParams(List<?> params) {
        this.position = (String) params.get(0);

    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (position == null) throw new IllegalStateException();
        if (chessBoard.at(position).getPiece().isPresent())
            throw new IllegalArgumentException("You should select an empty square");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        BlackHole piece = new BlackHole();
        chessBoard.add(piece, position);
        return true;
    }
}
