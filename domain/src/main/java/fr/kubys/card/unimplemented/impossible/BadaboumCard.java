package fr.kubys.card.unimplemented.impossible;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class BadaboumCard extends Card<NoCardParam> {

    public BadaboumCard() {
        super("Badaboum",
                "Si vous parvenez à faire tenir cette carte pendant au moins 5 secondes en équilibre sur votre Roi, vous pouvez soit récupérer une pièce que vous a précédemment prise votre adversaire et la placer sur la case de votre choix, soit rejouer.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Badaboum is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Badaboum is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Badaboum is not yet implemented");
    }
}
