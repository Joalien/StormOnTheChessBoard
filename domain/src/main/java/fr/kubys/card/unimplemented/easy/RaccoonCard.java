package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class RaccoonCard extends Card<NoCardParam> {

    public RaccoonCard() {
        super("Raton laveur",
                "Sur l'échiquier, permutez l'un de vos Pions avec l'un de vos Fous, ou l'un de vos Cavaliers, ou l'une de vos Tours.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Raton laveur is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Raton laveur is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Raton laveur is not yet implemented");
    }
}
