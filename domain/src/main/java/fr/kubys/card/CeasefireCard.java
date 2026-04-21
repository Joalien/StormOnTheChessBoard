package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.CeasefireEffect;
import fr.kubys.card.params.NoCardParam;

public class CeasefireCard extends Card<NoCardParam> implements Effectable<CeasefireEffect> {

    public CeasefireCard() {
        super("Cessez le Feu",
                "Il est désormais interdit aux deux joueurs de prendre des pièces adverses. Tous les déplacements doivent aboutir à une case vide. L'effet de cette carte dure jusqu'à ce que l'un des Rois soit mis en échec.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        // No validation needed
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        chessBoard.addEffect(new CeasefireEffect(chessBoard));
    }
}
