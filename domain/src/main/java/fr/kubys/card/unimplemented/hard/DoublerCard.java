package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class DoublerCard extends Card<NoCardParam> {

    public DoublerCard() {
        super("Doublure",
                "Si la case de départ de votre Roi est libre, vous pouvez y faire apparaître un second Roi. Désormais, vous pouvez laisser l'un de vos Rois en échec, et même vous le faire prendre. Vous ne perdrez la partie que lorsque votre dernier Roi sera mat.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Doublure is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Doublure is not yet implemented");
    }
}
