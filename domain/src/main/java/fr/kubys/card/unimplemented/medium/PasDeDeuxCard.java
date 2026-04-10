package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class PasDeDeuxCard extends Card<NoCardParam> {

    public PasDeDeuxCard() {
        super("Pas de deux",
                "Déplacez n'importe laquelle de vos pièces en lui faisant effectuer exactement le même mouvement que la pièce que vous venez de jouer. Cette seconde pièce déplacée peut prendre.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Pas de deux is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Pas de deux is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Pas de deux is not yet implemented");
    }
}
