package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class AstralTravelCard extends Card<NoCardParam> {

    public AstralTravelCard() {
        super("Voyage Astral",
                "Enlevez de l'échiquier l'une de vos pièces ou une pièce adverse, que vous remettez à sa place après votre déplacement. Vous pouvez donc franchir librement cette case, mais ne pouvez pas y arrêter la pièce que vous déplacez.",
                CardType.BEFORE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Voyage Astral is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Voyage Astral is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Voyage Astral is not yet implemented");
    }
}
