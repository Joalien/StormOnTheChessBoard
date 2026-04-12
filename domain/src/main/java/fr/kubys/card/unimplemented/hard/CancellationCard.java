package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class CancellationCard extends Card<NoCardParam> {

    public CancellationCard() {
        super("Annulation",
                "Le déplacement de votre adversaire est annulé. Il doit rejouer un autre coup, avec la même pièce ou une autre.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Annulation is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Annulation is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Annulation is not yet implemented");
    }
}
