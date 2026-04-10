package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class MeritPromotionCard extends Card<NoCardParam> {

    public MeritPromotionCard() {
        super("Promotion au Mérite",
                "L'un de vos pions est promu en une pièce de votre choix, alors qu'il ne se trouve que sur la sixième rangée.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Promotion au Mérite is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Promotion au Mérite is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Promotion au Mérite is not yet implemented");
    }
}
