package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class StateVisitCard extends Card<NoCardParam> {

    public StateVisitCard() {
        super("Visites Officielles",
                "Permutez votre Roi avec celui de votre adversaire, à condition que cela ne mette aucun de vous deux en échec.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Visites Officielles is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Visites Officielles is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Visites Officielles is not yet implemented");
    }
}
