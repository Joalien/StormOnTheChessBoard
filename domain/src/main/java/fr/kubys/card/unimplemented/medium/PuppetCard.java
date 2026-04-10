package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class PuppetCard extends Card<NoCardParam> {

    public PuppetCard() {
        super("Fantoche",
                "Jouez cette carte lorsque vous êtes mis en échec, même mat. Vous permutez alors sur l'échiquier votre Roi avec l'un de vos Pions.",
                CardType.BEFORE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Fantoche is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Fantoche is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Fantoche is not yet implemented");
    }
}
