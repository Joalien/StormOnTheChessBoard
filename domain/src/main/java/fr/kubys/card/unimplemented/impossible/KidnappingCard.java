package fr.kubys.card.unimplemented.impossible;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class KidnappingCard extends Card<NoCardParam> {

    public KidnappingCard() {
        super("Kidnapping",
                "Votre adversaire tourne la tête dix secondes, et vous subtilisez l'une de ses pièces de l'échiquier. Il a ensuite dix secondes pour retrouver quelle pièce a disparu et sur quelle case elle se trouvait. S'il trouve, la pièce est remise en place. S'il se trompe, elle est considérée comme prise.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Kidnapping is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Kidnapping is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Kidnapping is not yet implemented");
    }
}
