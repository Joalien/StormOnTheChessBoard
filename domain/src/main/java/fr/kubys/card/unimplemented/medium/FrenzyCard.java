package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class FrenzyCard extends Card<NoCardParam> {

    public FrenzyCard() {
        super("Fringale",
                "Les deux adversaires sont désormais obligés de prendre une pièce adverse à chaque coup. L'effet de cette carte dure jusqu'à ce que l'un des deux joueurs soit dans l'impossibilité de prendre.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Fringale is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Fringale is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Fringale is not yet implemented");
    }
}
