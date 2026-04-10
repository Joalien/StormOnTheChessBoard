package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class LeapfrogCard extends Card<NoCardParam> {

    public LeapfrogCard() {
        super("Saute Mouton",
                "Déplacez l'un de vos pions comme au jeu de Dames, en sautant sur les diagonales par dessus vos pièces ou celles de l'adversaire. Les pièces sautées ne sont pas prises et restent sur place.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Saute Mouton is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Saute Mouton is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Saute Mouton is not yet implemented");
    }
}
