package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class UncontrolledElementCard extends Card<NoCardParam> {

    public UncontrolledElementCard() {
        super("Élément Incontrôlé",
                "Déplacez une pièce adverse selon ses règles normales de déplacement. Vous ne pouvez pas prendre ainsi une pièce adverse.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Élément Incontrôlé is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Élément Incontrôlé is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Élément Incontrôlé is not yet implemented");
    }
}
