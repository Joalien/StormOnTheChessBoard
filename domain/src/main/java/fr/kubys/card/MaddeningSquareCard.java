package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.MaddeningSquareEffect;
import fr.kubys.card.params.PositionCardParam;

public class MaddeningSquareCard extends Card<PositionCardParam> implements Effectable<MaddeningSquareEffect> {

    public MaddeningSquareCard() {
        super("La Case qui Rend Fou",
                "Désignez une case inoccupée de l'échiquier. Jusqu'à la fin de la partie, toute pièce qui quitte cette case peut le faire en se déplaçant comme un Fou.",
                CardType.AFTER_TURN,
                PositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PositionCardParam param) {
        if (param.position() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (chessBoard.at(param.position()).getPiece().isPresent())
            throw new IllegalArgumentException("La case %s doit être inoccupée".formatted(param.position()));
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PositionCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PositionCardParam param) {
        chessBoard.addEffect(new MaddeningSquareEffect(chessBoard, param.position()));
    }
}
