package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class SuicideKnightCard extends Card<NoCardParam> {

    public SuicideKnightCard() {
        super("Cavalier Suicide",
                "Jouez cette carte lorsque l'un de vos Cavaliers a la possibilité de prendre plusieurs pièces adverses. Vous le faites alors éclater en plusieurs éléments qui vont prendre chacun l'une des pièces adverses se trouvant à portée. Le Cavalier ainsi éclaté est retiré du jeu.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cavalier Suicide is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cavalier Suicide is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cavalier Suicide is not yet implemented");
    }
}
