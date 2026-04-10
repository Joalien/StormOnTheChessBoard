package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class PoisonCard extends Card<NoCardParam> {

    public PoisonCard() {
        super("Poison",
                "Désignez l'une de vos pièces dont la chair est, définitivement, empoisonnée. Si une pièce adverse prend la pièce empoisonnée, elles sont toutes deux retirées de l'échiquier.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Poison is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Poison is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Poison is not yet implemented");
    }
}
