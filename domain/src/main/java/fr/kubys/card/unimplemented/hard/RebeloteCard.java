package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class RebeloteCard extends Card<NoCardParam> {

    public RebeloteCard() {
        super("Rebelote et dix de der",
                "Cette carte vous permet de jouer deux autres cartes à ce coup.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Rebelote et dix de der is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Rebelote et dix de der is not yet implemented");
    }
}
