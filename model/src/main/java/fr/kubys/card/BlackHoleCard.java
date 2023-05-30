package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.piece.extra.BlackHole;

public class BlackHoleCard extends Card<PositionCardParam> {

    private PositionCardParam param;

    public BlackHoleCard() {
        super("Trou noir", "Désignez une case vide qui est \"retirée\" de l'échiquier juqu'à la fin de la partie. Il sera impossible pendant la suite du jeu de s'y arrêter ou de la traverser.", CardType.AFTER_TURN);
    }

    @Override
    protected void setupParams(PositionCardParam params) {
        this.param = params;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (param.position() == null) throw new IllegalStateException();
        if (chessBoard.at(param.position()).getPiece().isPresent())
            throw new IllegalArgumentException("You should select an empty square");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard) {
        BlackHole piece = new BlackHole();
        chessBoard.add(piece, param.position());
    }
}
