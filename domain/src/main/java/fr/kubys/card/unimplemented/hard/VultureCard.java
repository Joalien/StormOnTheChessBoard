package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class VultureCard extends Card<NoCardParam> {

    public VultureCard() {
        super("Vautour",
                "Récupérez la carte que votre adversaire vient de jouer, et mettez-la dans votre main.",
                CardType.ENEMY_TURN_AFTER_CARD,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Vautour is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Vautour is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Vautour is not yet implemented");
    }
}
