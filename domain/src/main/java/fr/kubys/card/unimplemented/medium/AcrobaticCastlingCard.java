package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class AcrobaticCastlingCard extends Card<NoCardParam> {

    public AcrobaticCastlingCard() {
        super("Roque Acrobatique",
                "Quels que soient les antécédents et la situation actuelle, si vous avez votre Roi et l'une de vos Tours sur une même rangée ou colonne, et s'il n'y a aucune pièce entre eux, vous pouvez roquer en plaçant la Tour à côté du Roi, puis en faisant passer le Roi par dessus la Tour.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Roque Acrobatique is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Roque Acrobatique is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Roque Acrobatique is not yet implemented");
    }
}
