package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class YouTooBrutusCard extends Card<NoCardParam> {

    public YouTooBrutusCard() {
        super("Toi aussi, mon fils !",
                "Jouez cette carte lorsque votre adversaire vous annonce un échec, même mat. La pièce qui vous met en échec change alors de couleur et passe dans votre camp.",
                CardType.ENEMY_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Toi aussi, mon fils ! is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Toi aussi, mon fils ! is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Toi aussi, mon fils ! is not yet implemented");
    }
}
