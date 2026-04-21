package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.EffectCardParam;

public class TrashCanCard extends Card<EffectCardParam> {

    public TrashCanCard() {
        super("Poubelle",
                "Cette carte annule une carte à effet continu ou retardé jouée plus tôt dans la partie.",
                CardType.AFTER_TURN,
                EffectCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, EffectCardParam param) {
        if (param.effect() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!chessBoard.getEffects().contains(param.effect()))
            throw new IllegalArgumentException("Cet effet n'est pas actif");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, EffectCardParam param) {
        chessBoard.removeEffect(param.effect());
    }
}
