package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class SeniorityPromotionCard extends Card<NoCardParam> {

    public SeniorityPromotionCard() {
        super("Promotion à l'ancienneté",
                "S'il ne vous reste plus qu'un ou deux pions sur l'échiquier, vous pouvez immédiatement les promouvoir en d'autres pièces de votre choix, Dame exceptée.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Promotion à l'ancienneté is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Promotion à l'ancienneté is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Promotion à l'ancienneté is not yet implemented");
    }
}
