package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class ZombiesCard extends Card<NoCardParam> {

    public ZombiesCard() {
        super("Zombies",
                "Cette carte remet en jeu deux pions de chaque joueur. Chacun à son tour, en commençant par votre adversaire, replace l'un de ses pions perdus sur l'une des cases libres de la seconde rangée.",
                CardType.BEFORE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Zombies is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Zombies is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Zombies is not yet implemented");
    }
}
