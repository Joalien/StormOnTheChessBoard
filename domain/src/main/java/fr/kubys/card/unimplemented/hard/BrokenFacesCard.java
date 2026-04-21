package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class BrokenFacesCard extends Card<NoCardParam> {

    public BrokenFacesCard() {
        super("Gueules Cassées",
                "Jouez cette carte lorsque votre adversaire vient de vous prendre une pièce (pas un Pion). Regardez alors les cartes de la pioche et choisissez-en une, que vous mettez dans votre jeu à la place de celle-ci.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Gueules Cassées is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Gueules Cassées is not yet implemented");
    }
}
