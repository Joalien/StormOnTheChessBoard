package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class FogCard extends Card<NoCardParam> {

    public FogCard() {
        super("Brouillard",
                "Jouez cette carte lorsque votre adversaire vient de jouer un Fou, une Tour ou une Dame. Sa pièce ne peut effectuer le mouvement prévu, et s'immobilise après avoir avancé d'une case dans la direction voulue.",
                CardType.ENEMY_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Brouillard is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Brouillard is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Brouillard is not yet implemented");
    }
}
