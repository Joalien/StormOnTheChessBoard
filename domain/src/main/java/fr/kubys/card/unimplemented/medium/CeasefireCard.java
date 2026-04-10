package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class CeasefireCard extends Card<NoCardParam> {

    public CeasefireCard() {
        super("Cessez le Feu",
                "Il est désormais interdit aux deux joueurs de prendre des pièces adverses. Tous les déplacements doivent aboutir à une case vide. L'effet de cette carte dure jusqu'à ce que l'un des Rois soit mis en échec.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cessez le Feu is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cessez le Feu is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cessez le Feu is not yet implemented");
    }
}
