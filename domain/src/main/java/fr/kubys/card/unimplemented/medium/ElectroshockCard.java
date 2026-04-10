package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class ElectroshockCard extends Card<NoCardParam> {

    public ElectroshockCard() {
        super("Électrochoc",
                "Tous les Fous qui peuvent l'être doivent être déplacés horizontalement ou verticalement d'une case, sans prendre de pièce. Votre adversaire déplace ses Fous en premier.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Électrochoc is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Électrochoc is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Électrochoc is not yet implemented");
    }
}
