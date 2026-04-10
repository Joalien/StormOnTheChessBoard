package fr.kubys.card.unimplemented.easy;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class YouOnlyLiveTwiceCard extends Card<NoCardParam> {

    public YouOnlyLiveTwiceCard() {
        super("On ne vit que deux fois",
                "Récupérez l'une des pièces (sauf la Dame) que vous a prises votre adversaire, et placez-la sur une case libre où elle pouvait se trouver en début de partie.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("On ne vit que deux fois is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("On ne vit que deux fois is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("On ne vit que deux fois is not yet implemented");
    }
}
