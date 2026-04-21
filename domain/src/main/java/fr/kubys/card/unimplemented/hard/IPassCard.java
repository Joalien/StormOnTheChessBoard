package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class IPassCard extends Card<NoCardParam> {

    public IPassCard() {
        super("Je Passe",
                "Lorsque vous jouez cette carte, vous passez votre tour, et gardez cette carte visible devant vous. Vous pourrez l'utiliser plus tard, une seule fois, pour rejouer après votre tour.",
                CardType.BEFORE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Je Passe is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Je Passe is not yet implemented");
    }
}
