package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class NonViolentCard extends Card<NoCardParam> {

    public NonViolentCard() {
        super("Non Violent",
                "L'une de vos pièces, excepté le Roi, devient non violente jusqu'à la fin de la partie. Cette pièce ne peut plus prendre de pièce adverse, ni être prise, mais elle peut toujours donner échec.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Non Violent is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Non Violent is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Non Violent is not yet implemented");
    }
}
