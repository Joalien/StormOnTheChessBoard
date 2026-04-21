package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class NoSirCard extends Card<NoCardParam> {

    public NoSirCard() {
        super("Que Nenni !",
                "Cette carte annule toute autre carte au moment où elle est jouée. Jouez-la juste après la carte de votre adversaire que vous voulez annuler.",
                CardType.ENEMY_TURN_AFTER_CARD,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Que Nenni ! is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Que Nenni ! is not yet implemented");
    }
}
