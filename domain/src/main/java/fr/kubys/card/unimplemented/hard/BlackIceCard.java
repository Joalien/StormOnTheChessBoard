package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class BlackIceCard extends Card<NoCardParam> {

    public BlackIceCard() {
        super("Verglas",
                "Jouez cette carte lorsque votre adversaire vient de jouer un Fou, une Tour ou une Dame sans effectuer de capture. Cette pièce doit poursuivre son déplacement au-delà, jusqu'à ce qu'elle capture l'une de vos pièces, s'immobilise devant une pièce de votre adversaire, ou tombe au-delà du bord de l'échiquier.",
                CardType.ENEMY_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Verglas is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Verglas is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Verglas is not yet implemented");
    }
}
