package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class DunceCapCard extends Card<NoCardParam> {

    public DunceCapCard() {
        super("Bonnet d'Âne",
                "Désignez une pièce adverse (sauf le Roi), que vous envoyez au piquet sur une case libre située dans l'un des quatre coins de l'échiquier. Votre adversaire ne pourra pas déplacer cette pièce à son prochain tour.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Bonnet d'Âne is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Bonnet d'Âne is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Bonnet d'Âne is not yet implemented");
    }
}
