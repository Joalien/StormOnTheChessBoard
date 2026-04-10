package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class SerialKillerCard extends Card<NoCardParam> {

    public SerialKillerCard() {
        super("Serial Killer",
                "L'un de vos Pions, qui vient de prendre une pièce adverse, est pris d'une frénésie destructrice. Continuez à déplacer, en diagonale, le même pion en prenant à chaque déplacement une pièce adverse, tant que cela est possible.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Serial Killer is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Serial Killer is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Serial Killer is not yet implemented");
    }
}
