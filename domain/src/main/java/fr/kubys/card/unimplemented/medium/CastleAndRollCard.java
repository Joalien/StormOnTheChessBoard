package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class CastleAndRollCard extends Card<NoCardParam> {

    public CastleAndRollCard() {
        super("Roque et Rolle",
                "Si aucune pièce adverse ne se trouve sur votre première rangée, vous pouvez réarranger comme vous le souhaitez toutes vos pièces qui s'y trouvent.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Roque et Rolle is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Roque et Rolle is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Roque et Rolle is not yet implemented");
    }
}
