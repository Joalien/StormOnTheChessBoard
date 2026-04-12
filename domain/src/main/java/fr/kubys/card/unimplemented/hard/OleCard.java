package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class OleCard extends Card<NoCardParam> {

    public OleCard() {
        super("Olé !",
                "Jouez cette carte lorsque votre adversaire veut prendre l'une de vos pièces avec un Fou, une Tour ou une Dame. La pièce attaquée reste en place. La pièce attaquante passe au travers et doit poursuivre son déplacement jusqu'à ce qu'elle capture une autre de vos pièces, s'immobilise devant une autre pièce adverse, ou tombe au-delà du bord de l'échiquier.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Olé ! is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Olé ! is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Olé ! is not yet implemented");
    }
}
