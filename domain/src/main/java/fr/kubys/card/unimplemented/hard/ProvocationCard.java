package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class ProvocationCard extends Card<NoCardParam> {

    public ProvocationCard() {
        super("Provocation",
                "Votre adversaire doit, ce coup-ci, capturer une de vos pièces. S'il ne peut ou ne veut pas le faire, il passe son tour.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Provocation is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Provocation is not yet implemented");
    }
}
