package fr.kubys.card.unimplemented.impossible;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class OmertaCard extends Card<NoCardParam> {

    public OmertaCard() {
        super("Omerta",
                "Le prochain joueur à prononcer le nom d'une pièce, excepté le Roi, doit perdre une pièce de ce type. S'il ne possède plus la pièce correspondante, cette carte continue à faire effet.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Omerta is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Omerta is not yet implemented");
    }
}
