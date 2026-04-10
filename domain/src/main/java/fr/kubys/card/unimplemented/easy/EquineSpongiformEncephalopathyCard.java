package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class EquineSpongiformEncephalopathyCard extends Card<NoCardParam> {

    public EquineSpongiformEncephalopathyCard() {
        super("Encéphalopathie Spongiforme Équine",
                "L'un de vos Cavaliers, ou un Cavalier adverse, devient Fou. Il se déplacera désormais uniquement comme un Fou.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Encéphalopathie Spongiforme Équine is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Encéphalopathie Spongiforme Équine is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Encéphalopathie Spongiforme Équine is not yet implemented");
    }
}
