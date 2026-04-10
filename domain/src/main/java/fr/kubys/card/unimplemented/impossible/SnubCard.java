package fr.kubys.card.unimplemented.impossible;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class SnubCard extends Card<NoCardParam> {

    public SnubCard() {
        super("Camouflet",
                "Jouez cette carte lorsque votre adversaire fait un geste ou une réflexion qui vous paraît désobligeant. Vous enlevez alors l'un de ses pions de l'échiquier.",
                CardType.ENEMY_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Camouflet is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Camouflet is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Camouflet is not yet implemented");
    }
}
