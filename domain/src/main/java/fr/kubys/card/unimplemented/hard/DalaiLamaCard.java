package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class DalaiLamaCard extends Card<NoCardParam> {

    public DalaiLamaCard() {
        super("Dalaï Lama",
                "La pièce que votre adversaire vient de prendre se réincarne immédiatement en une autre pièce identique, qui apparaît sur une case libre où elle pouvait se trouver en début de partie.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Dalaï Lama is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Dalaï Lama is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Dalaï Lama is not yet implemented");
    }
}
