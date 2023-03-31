package card;

import piece.BlackHole;
import board.ChessBoard;

public class BlackHoleCard extends SCCard {

    private final String position;

    public BlackHoleCard(String position) {
        super("Trou noir", "Désignez une case vide qui est \"retirée\" de l'échiquier juqu'à la fin de la partie. Il sera impossible pendant la suite du jeu de s'y arrêter ou de la traverser.", SCType.AFTER_TURN);
        this.position = position;
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
