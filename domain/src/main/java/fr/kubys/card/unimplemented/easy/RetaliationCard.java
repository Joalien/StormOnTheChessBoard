package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class RetaliationCard extends Card<NoCardParam> {

    public RetaliationCard() {
        super("Représailles",
                "Jouez cette carte lorsque votre adversaire vient de vous prendre une pièce (pas un Pion). Vous vous vengez alors en retirant l'un de ses pions de l'échiquier.",
                CardType.ENEMY_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Représailles is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Représailles is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Représailles is not yet implemented");
    }
}
