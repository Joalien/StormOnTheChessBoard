package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class DoubleGameCard extends Card<NoCardParam> {

    public DoubleGameCard() {
        super("Double Jeu",
                "Pour trois tours, échangez votre place avec celle de votre adversaire, chacun gardant son jeu de cartes. Durant ces trois tours, il est tout à fait possible à chacun de gagner avec sa nouvelle couleur.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Double Jeu is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Double Jeu is not yet implemented");
    }
}
