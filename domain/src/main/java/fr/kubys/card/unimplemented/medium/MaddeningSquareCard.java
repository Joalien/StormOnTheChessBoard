package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class MaddeningSquareCard extends Card<NoCardParam> {

    public MaddeningSquareCard() {
        super("La Case qui Rend Fou",
                "Désignez une case inoccupée de l'échiquier. Jusqu'à la fin de la partie, toute pièce qui quitte cette case peut le faire en se déplaçant comme un Fou.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("La Case qui Rend Fou is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("La Case qui Rend Fou is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("La Case qui Rend Fou is not yet implemented");
    }
}
