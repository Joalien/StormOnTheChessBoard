package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class ModestyCard extends Card<NoCardParam> {

    public ModestyCard() {
        super("Modestie",
                "Déplacez n'importe laquelle de vos pièces à la manière d'un Pion. Si vous amenez ainsi une pièce sur la dernière rangée, vous pouvez la promouvoir en toute autre pièce de votre choix, comme un Pion.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Modestie is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Modestie is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Modestie is not yet implemented");
    }
}
