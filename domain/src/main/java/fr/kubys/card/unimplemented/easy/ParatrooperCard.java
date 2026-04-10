package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class ParatrooperCard extends Card<NoCardParam> {

    public ParatrooperCard() {
        super("Parachutiste",
                "Parachutez l'un des Pions que vous a pris votre adversaire sur l'une des quatre cases centrales de l'échiquier. Cette case doit être inoccupée.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Parachutiste is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Parachutiste is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Parachutiste is not yet implemented");
    }
}
