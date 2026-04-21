package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.OriginsEffect;
import fr.kubys.card.params.NoCardParam;

public class OriginsCard extends Card<NoCardParam> implements Effectable<OriginsEffect> {

    public OriginsCard() {
        super("Origines",
                "Aucun joueur ne peut plus jouer de carte de Tempête sur l'échiquier. L'effet de cette carte dure jusqu'à ce que l'un des Rois soit mis en échec.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        // Always valid
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        chessBoard.addEffect(new OriginsEffect());
    }
}
