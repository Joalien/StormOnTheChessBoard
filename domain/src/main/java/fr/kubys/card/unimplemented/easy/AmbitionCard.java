package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class AmbitionCard extends Card<NoCardParam> {

    public AmbitionCard() {
        super("Ambition",
                "Remplacez l'un de vos pions par l'une des pièces, sauf la Dame, que vous a prises votre adversaire.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Ambition is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Ambition is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Ambition is not yet implemented");
    }
}
