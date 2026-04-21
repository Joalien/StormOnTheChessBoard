package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.ShieldEffect;
import fr.kubys.card.params.NoCardParam;

public class ShieldCard extends Card<NoCardParam> implements Effectable<ShieldEffect> {

    public ShieldCard() {
        super("Bouclier",
                "Votre adversaire ne peut pas vous prendre ce tour-ci la pièce que vous venez de déplacer.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        if (chessBoard.getLastMovedPiece() == null)
            throw new IllegalStateException("Aucun déplacement n'a été effectué ce tour");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        chessBoard.addEffect(new ShieldEffect(chessBoard.getLastMovedPiece(), chessBoard.getTurnNumber()));
    }
}
