package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class RegencyCard extends Card<NoCardParam> {

    public RegencyCard() {
        super("Régence",
                "Vous faites disparaître votre Roi de l'échiquier pour un tour. Avant votre prochain coup, vous devrez le replacer sur une case libre de votre choix, au bord de l'échiquier.",
                CardType.BEFORE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Régence is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Régence is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Régence is not yet implemented");
    }
}
