package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class KickCard extends Card<NoCardParam> {

    public KickCard() {
        super("Ruade",
                "Annulez le coup que vient de jouer votre adversaire avec l'un de ses Cavaliers. Déplacez vous-même à sa place ce même Cavalier. Vous pouvez même prendre ainsi une pièce adverse... ou l'une de vos pièces.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Ruade is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Ruade is not yet implemented");
    }
}
