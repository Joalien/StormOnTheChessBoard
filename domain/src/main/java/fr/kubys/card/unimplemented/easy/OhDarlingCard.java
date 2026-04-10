package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class OhDarlingCard extends Card<NoCardParam> {

    public OhDarlingCard() {
        super("Oh, Darling !",
                "Amenez directement votre Roi sur une case libre adjacente à votre Dame, ou votre Dame sur une case libre adjacente à votre Roi.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Oh, Darling ! is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Oh, Darling ! is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Oh, Darling ! is not yet implemented");
    }
}
