package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class CopycatCard extends Card<NoCardParam> {

    public CopycatCard() {
        super("Vous en êtes un Autre",
                "Cette carte reproduit l'effet de la dernière carte jouée par votre adversaire.",
                CardType.ENEMY_TURN_AFTER_CARD,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Vous en êtes un Autre is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Vous en êtes un Autre is not yet implemented");
    }
}
