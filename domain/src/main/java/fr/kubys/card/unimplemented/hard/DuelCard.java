package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class DuelCard extends Card<NoCardParam> {

    public DuelCard() {
        super("Duel",
                "Jouez cette carte lorsque votre adversaire veut prendre l'une de vos pièces. Le combat entre les deux pièces est résolu au 421, à pile ou face, ou à Pierre, feuille, ciseaux, selon votre choix.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Duel is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Duel is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Duel is not yet implemented");
    }
}
