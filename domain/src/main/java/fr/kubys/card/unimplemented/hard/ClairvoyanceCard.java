package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class ClairvoyanceCard extends Card<NoCardParam> {

    public ClairvoyanceCard() {
        super("Voyance",
                "Notez sur un morceau de papier la pièce que, selon vous, votre adversaire devrait jouer ce tour-ci. S'il déplace effectivement cette pièce, son déplacement est annulé et votre adversaire ne peut rien faire d'autre à ce tour.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Voyance is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Voyance is not yet implemented");
    }
}
