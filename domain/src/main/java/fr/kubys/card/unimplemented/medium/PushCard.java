package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class PushCard extends Card<NoCardParam> {

    public PushCard() {
        super("Poussée",
                "L'un de vos pions avance sur une case occupée, en poussant devant lui la pièce qui s'y trouve. Une pièce poussée hors de l'échiquier est retirée du jeu. Le Roi ne peut pas être poussé.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Poussée is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Poussée is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Poussée is not yet implemented");
    }
}
