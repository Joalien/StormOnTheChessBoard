package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class ShotCard extends Card<NoCardParam> {

    public ShotCard() {
        super("Tir",
                "Vous pouvez prendre une pièce adverse au tir, sans que son agresseur vienne prendre sa place. Le coup consiste à désigner une case que votre pièce pourrait atteindre, puis à retirer la pièce adverse qui s'y trouve.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Tir is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Tir is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Tir is not yet implemented");
    }
}
