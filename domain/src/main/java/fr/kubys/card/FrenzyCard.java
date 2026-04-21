package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.FrenzyEffect;
import fr.kubys.card.params.NoCardParam;

public class FrenzyCard extends Card<NoCardParam> implements Effectable<FrenzyEffect> {

    public FrenzyCard() {
        super("Fringale",
                "Les deux adversaires sont désormais obligés de prendre une pièce adverse à chaque coup. L'effet de cette carte dure jusqu'à ce que l'un des deux joueurs soit dans l'impossibilité de prendre.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        // No validation needed
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        chessBoard.addEffect(new FrenzyEffect(chessBoard));
    }
}
