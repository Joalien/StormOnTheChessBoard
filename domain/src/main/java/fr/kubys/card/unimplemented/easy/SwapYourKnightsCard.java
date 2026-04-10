package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class SwapYourKnightsCard extends Card<NoCardParam> {

    public SwapYourKnightsCard() {
        super("Changez vos Cavaliers",
                "Sur l'échiquier, permutez l'un de vos Cavaliers avec un Cavalier adverse.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Changez vos Cavaliers is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Changez vos Cavaliers is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Changez vos Cavaliers is not yet implemented");
    }
}
