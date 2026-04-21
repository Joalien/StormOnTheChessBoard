package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class CohabitationCard extends Card<NoCardParam> {

    public CohabitationCard() {
        super("Cohabitation",
                "Déplacez l'une de vos pièces pour l'amener sur une case où se trouve déjà une autre de vos pièces. Vous pourrez par la suite déplacer normalement l'une ou l'autre, mais pas les deux à la fois. Si votre adversaire amène une pièce sur cette case, il prend vos deux pièces.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cohabitation is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cohabitation is not yet implemented");
    }
}
