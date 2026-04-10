package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class GuardianAngelCard extends Card<NoCardParam> {

    public GuardianAngelCard() {
        super("Ange Gardien",
                "Jouez cette carte lorsque vous êtes en échec, même mat. Vous reprenez alors l'une des pièces que vous a prises votre adversaire pour l'intercaler sur une case libre entre votre Roi et la pièce qui le menace.",
                CardType.ENEMY_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Ange Gardien is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Ange Gardien is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Ange Gardien is not yet implemented");
    }
}
