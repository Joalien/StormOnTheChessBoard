package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class CustomsCard extends Card<NoCardParam> {

    public CustomsCard() {
        super("Douane",
                "Jouez cette carte lorsque votre adversaire vient de faire franchir à l'une de ses pièces la frontière séparant votre moitié de l'échiquier de la sienne. Il doit alors payer en perdant un de ses pions, de son choix, ou renoncer à son coup.",
                CardType.ENEMY_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Douane is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Douane is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Douane is not yet implemented");
    }
}
