package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class StrawManCard extends Card<NoCardParam> {

    public StrawManCard() {
        super("Homme de Paille",
                "Sacrifiez l'un de vos Pions pour sauver la pièce que votre adversaire vient de vous prendre. Remettez la pièce sur la case où se trouvait le pion sacrifié.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Homme de Paille is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Homme de Paille is not yet implemented");
    }
}
