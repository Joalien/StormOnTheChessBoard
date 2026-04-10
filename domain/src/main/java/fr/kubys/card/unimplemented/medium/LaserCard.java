package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class LaserCard extends Card<NoCardParam> {

    public LaserCard() {
        super("Laser",
                "L'une de vos pièces (Tour, Fou ou Dame) envoie un rayon laser dans l'une de ses directions possibles de déplacement. Toutes les pièces, amies ou ennemies, qui se trouvent dans cette direction sont désintégrées. Les Rois ne sont pas affectés par le rayon laser, mais ne l'arrêtent pas.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Laser is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Laser is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Laser is not yet implemented");
    }
}
