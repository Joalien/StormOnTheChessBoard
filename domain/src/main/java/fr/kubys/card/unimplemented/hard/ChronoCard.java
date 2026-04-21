package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class ChronoCard extends Card<NoCardParam> {

    public ChronoCard() {
        super("Chrono",
                "Votre adversaire a 15 secondes, montre en main, pour jouer son prochain coup. Passé ce temps, il perd son tour de jeu.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Chrono is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Chrono is not yet implemented");
    }
}
