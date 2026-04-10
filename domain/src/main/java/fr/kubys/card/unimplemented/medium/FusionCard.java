package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class FusionCard extends Card<NoCardParam> {

    public FusionCard() {
        super("Fusion",
                "Déplacez l'une de vos pièces vers une case occupée par une autre de vos pièces. Les deux pièces fusionnent en une nouvelle, qui se déplacera désormais comme l'une ou l'autre des deux pièces d'origine. Le Roi ne peut pas fusionner.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Fusion is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Fusion is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Fusion is not yet implemented");
    }
}
