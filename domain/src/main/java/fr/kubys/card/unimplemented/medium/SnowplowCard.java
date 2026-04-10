package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class SnowplowCard extends Card<NoCardParam> {

    public SnowplowCard() {
        super("Chasse Neige",
                "Déplacez une Tour, une Dame ou un Fou sur l'une de ses lignes de déplacement en prenant toutes les pièces adverses qui se trouvent sur son chemin. Votre pièce ne s'arrête que lorsqu'elle rencontre une pièce amie, ou le bord de l'échiquier.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Chasse Neige is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Chasse Neige is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Chasse Neige is not yet implemented");
    }
}
